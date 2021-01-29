package uk.gov.ons.census.fwmt.csvservice.implementation.nc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.NCIntListing;
import uk.gov.ons.census.fwmt.csvservice.message.RmFieldRepublishProducer;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Component("NC")
public class NonComplianceService implements CSVConverterService {

  @Value("${gcpBucket.directory}")
  private String directory;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  @Autowired
  private RmFieldRepublishProducer rmFieldRepublishProducer;

  public static final String CSV_NON_COMPLIANCE_REQUEST_EXTRACTED = "CSV_NON_COMPLIANCE_REQUEST_EXTRACTED";

  public static final String CANONICAL_NON_COMPLIANCE_CREATE_SENT = "CANONICAL_NON_COMPLIANCE_CREATE_SENT";

  @Override
  public void convertToCanonical() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = dateTimeFormatter.format(now);
    List<URI> nonComplianceFiles = storageUtils.getFilenamesInFolder(URI.create(directory), "NC");

    CsvToBean<NCIntListing> csvToBean;
    for (URI uri : nonComplianceFiles) {
      InputStream inputStream = storageUtils.getFileInputStream(uri);
      csvToBean = createCsvBean(inputStream);

      processObject(csvToBean);
      storageUtils.move(uri, URI.create(directory + "/processed/" + "NC-processed-" + timestamp));
    }
  }

  private CsvToBean<NCIntListing> createCsvBean(InputStream inputStream) {
    CsvToBean<NCIntListing> csvToBean;
    csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .withSeparator('|')
        .withType(NCIntListing.class)
        .build();

    return csvToBean;
  }

  private void processObject(CsvToBean<NCIntListing> csvToBean) {
    for (NCIntListing ncIntListing : csvToBean) {
      gatewayEventManager.triggerEvent(String.valueOf(ncIntListing.getCaseId()), CSV_NON_COMPLIANCE_REQUEST_EXTRACTED);
      createAndSendJob(ncIntListing);
    }
  }

  private void createAndSendJob(NCIntListing ncIntListing) {
    FwmtActionInstruction fwmtActionInstruction = NonComplianceBuilder.createNcJob(ncIntListing);
    rmFieldRepublishProducer.republish(fwmtActionInstruction);
        gatewayEventManager.triggerEvent(String.valueOf(fwmtActionInstruction.getCaseId()), CANONICAL_NON_COMPLIANCE_CREATE_SENT,
            "Case reference", fwmtActionInstruction.getCaseRef());
  }
}

