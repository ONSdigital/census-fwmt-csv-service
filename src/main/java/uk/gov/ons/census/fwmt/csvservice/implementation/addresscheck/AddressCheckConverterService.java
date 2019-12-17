package uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck;

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

  @Value("${gcpBucket.addresschecklocation}")
  private Resource csvGCPFile;

  @Value("${gcpBucket.addressCheckBucket}")
  private String bucketName;

  @Value("${gcpBucket.addressCheckBlob}")
  private String blobName;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private CsvServiceUtils csvServiceUtils;

  @Override
  public void convertToCanonical() throws GatewayException {
    CsvToBean<AddressCheckListing> csvToBean;
    try {
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(csvGCPFile.getInputStream(), StandardCharsets.UTF_8))
          .withType(AddressCheckListing.class)
          .build();

    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    for (AddressCheckListing addressCheckListing : csvToBean) {
      CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createAddressCheckJob(addressCheckListing);
      gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_ADDRESS_CHECK_CREATE_SENT);
      gatewayEventManager
          .triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()), CSV_ADDRESS_CHECK_REQUEST_EXTRACTED);
    }
    csvServiceUtils.moveCsvFile(bucketName, blobName);
  }
}
