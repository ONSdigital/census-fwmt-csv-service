package uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckCanonicalBuilder.createAddressCheckJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckGatewayEventsConfig.CANONICAL_ADDRESS_CHECK_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckGatewayEventsConfig.CSV_ADDRESS_CHECK_REQUEST_EXTRACTED;

@Component("AC")
public class AddressCheckConverterService implements CSVConverterService {

  @Value("${gcpBucket.addressCheckBucket}")
  private String bucketName;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private CsvServiceUtils csvServiceUtils;

  @Autowired
  private Storage googleCloudStorage;

  @Override
  public void convertToCanonical() throws GatewayException {

    Bucket startBucket = googleCloudStorage.get(bucketName);
    Page<Blob> blobPage = startBucket.list(Storage.BlobListOption.prefix("AC"));

    CsvToBean<AddressCheckListing> csvToBean = null;
    for (Blob blob : blobPage.iterateAll()) {
      csvToBean = createCsvBean(blob);
      processObject(csvToBean);
    }

    csvServiceUtils.moveCsvFile(bucketName, "AC");
  }

  private CsvToBean<AddressCheckListing> createCsvBean(Blob blob) {

    CsvToBean<AddressCheckListing> csvToBean;
    try {
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(addressCheckFile.getInputStream(), StandardCharsets.UTF_8))
          .withType(AddressCheckListing.class)
          .build();

    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }
    return csvToBean;
  }

  private void processObject(CsvToBean<AddressCheckListing> csvToBean) {
    for (AddressCheckListing addressCheckListing : csvToBean) {
      CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createAddressCheckJob(addressCheckListing);
      gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_ADDRESS_CHECK_CREATE_SENT);
      gatewayEventManager
          .triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()), CSV_ADDRESS_CHECK_REQUEST_EXTRACTED);
    }
  }
}
