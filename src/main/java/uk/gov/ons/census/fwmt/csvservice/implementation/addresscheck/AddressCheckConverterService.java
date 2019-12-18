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
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;

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
    Bucket bucket = googleCloudStorage.get(bucketName);
    String AC = "AC";
    Page<Blob> blobPage = bucket.list(Storage.BlobListOption.prefix(AC));

    CsvToBean<AddressCheckListing> csvToBean;
    for (Blob blob : blobPage.iterateAll()) {
      csvToBean = createCsvBean(blob);
      processObject(csvToBean);
    }
    csvServiceUtils.moveCsvFile(bucketName, AC);
  }

  private CsvToBean<AddressCheckListing> createCsvBean(Blob blob) {
    ReadChannel reader = blob.reader();
    InputStream inputStream = Channels.newInputStream(reader);

    CsvToBean<AddressCheckListing> csvToBean;
    csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream))
        .withType(AddressCheckListing.class)
        .build();

    return csvToBean;
  }

  private void processObject(CsvToBean<AddressCheckListing> csvToBean) throws GatewayException {
    for (AddressCheckListing addressCheckListing : csvToBean) {
      CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createAddressCheckJob(addressCheckListing);
      gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_ADDRESS_CHECK_CREATE_SENT);
      gatewayEventManager
          .triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()), CSV_ADDRESS_CHECK_REQUEST_EXTRACTED);
    }
  }
}
