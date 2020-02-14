package uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.dto.PostcodeLookup;
import uk.gov.ons.census.fwmt.csvservice.dto.RejectionReportEntry;
import uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader.RejectionProcessor;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.csvservice.service.LookupFileLoaderService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.FAILED_MATCH_POSTCODE;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.LOOKUP_FILE_MISSING_DATA;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckCanonicalBuilder.createAddressCheckJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckGatewayEventsConfig.CANONICAL_ADDRESS_CHECK_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckGatewayEventsConfig.CSV_ADDRESS_CHECK_REQUEST_EXTRACTED;

@Component("AC")
public class AddressCheckConverterService implements CSVConverterService {

  @Value("${gcpBucket.directory}")
  private String directory;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  @Autowired
  private LookupFileLoaderService lookupFileLoaderService;

  @Autowired
  private RejectionProcessor rejectionProcessor;

  private Map<String, PostcodeLookup> postcodeLookupMap;

  private List<AddressCheckListing> rejectedAddressCheckListing = new ArrayList<>();

  private List<RejectionReportEntry> rejectedReportAddressCheckListing = new ArrayList<>();

  @Override
  public void convertToCanonical() throws GatewayException {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyMMddHmmss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = dateTimeFormatter.format(now);
    postcodeLookupMap = lookupFileLoaderService.getLookupMap();
    List<URI> addressCheckFiles = storageUtils.getFilenamesInFolder(URI.create(directory), "AC");

    CsvToBean<AddressCheckListing> csvToBean;
    for (URI uri : addressCheckFiles) {
      InputStream inputStream = storageUtils.getFileInputStream(uri);
      csvToBean = createCsvBean(inputStream);
      processObject(csvToBean);
      storageUtils.move(uri, URI.create(directory + "/processed/" + "AC-processed-" + timestamp));
    }
    if (!rejectedAddressCheckListing.isEmpty()) {
      rejectionProcessor.createErrorReports(rejectedAddressCheckListing, rejectedReportAddressCheckListing);
    }
  }

  private CsvToBean<AddressCheckListing> createCsvBean(InputStream inputStream) {
    CsvToBean<AddressCheckListing> csvToBean;
    csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .withSeparator('|')
        .withType(AddressCheckListing.class)
        .build();

    return csvToBean;
  }

  private void processObject(CsvToBean<AddressCheckListing> csvToBean) throws GatewayException {
    for (AddressCheckListing addressCheckListing : csvToBean) {
      if (postcodeLookupMap.containsKey(getPostcode(addressCheckListing))) {
        if (addressCheckListingIsValid(postcodeLookupMap.get(getPostcode(addressCheckListing)))) {
          createAndSendJob(addressCheckListing);
        } else {
          appendToRejectionReport(addressCheckListing, "Lookup file missing data", LOOKUP_FILE_MISSING_DATA);
        }
      } else if (!postcodeLookupMap.containsKey(getPostcode(addressCheckListing))) {
        appendToRejectionReport(addressCheckListing, "Failed to match postcode: " + addressCheckListing.getPostcode(),
            FAILED_MATCH_POSTCODE);
      }
    }
  }

  private void appendToRejectionReport(AddressCheckListing addressCheckListing, String reason, String errorEvent) {
    RejectionReportEntry rejectionReportEntry = createRejectionReportEntry(addressCheckListing.getCaseReference(),
        reason);
    rejectedReportAddressCheckListing.add(rejectionReportEntry);
    rejectedAddressCheckListing.add(addressCheckListing);
    gatewayEventManager.triggerErrorEvent(this.getClass(), reason, "N/A", errorEvent);
  }

  private void createAndSendJob(AddressCheckListing addressCheckListing) throws GatewayException {
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createAddressCheckJob(addressCheckListing,
        postcodeLookupMap.get(getPostcode(addressCheckListing)));
    gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_ADDRESS_CHECK_CREATE_SENT);
    gatewayEventManager.triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()),
        CSV_ADDRESS_CHECK_REQUEST_EXTRACTED);
  }

  private String getPostcode(AddressCheckListing addressCheckListing) {
    return addressCheckListing.getPostcode().replaceAll("\\s+", "").toUpperCase();
  }

  private boolean addressCheckListingIsValid(PostcodeLookup postcodeLookup) {
    return !postcodeLookup.getAreaRoleId().isBlank() && !postcodeLookup.getLa().isBlank() && !postcodeLookup.getLaName()
        .isBlank();
  }

  private RejectionReportEntry createRejectionReportEntry(String caseRef, String reason) {
    RejectionReportEntry rejectionReportEntry = new RejectionReportEntry();
    rejectionReportEntry.setCaseRef(caseRef);
    rejectionReportEntry.setReason(reason);
    return rejectionReportEntry;
  }
}
