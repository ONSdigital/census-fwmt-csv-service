package uk.gov.ons.census.fwmt.csvservice.implementation.ce;

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
import uk.gov.ons.census.fwmt.csvservice.dto.CEJobListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CECanonicalBuilder.createCEJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CEGatewayEventsConfig.CANONICAL_CE_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CEGatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;

@Component("CE")
public class CEConverterService implements CSVConverterService {

  @Value("${gcpBucket.celocation}")
  private Resource csvGCPFile;

  @Value("${gcpBucket.ceBucket}")
  private String bucketName;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private CsvServiceUtils csvServiceUtils;

  @Override
  public void convertToCanonical() throws GatewayException {
    CsvToBean<CEJobListing> csvToBean;
    try {
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(csvGCPFile.getInputStream(), StandardCharsets.UTF_8))
          .withType(CEJobListing.class)
          .build();

    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    for (CEJobListing CEJobListing : csvToBean) {
      CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createCEJob(CEJobListing);
      gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_CE_CREATE_SENT);
      gatewayEventManager
          .triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()), CSV_CE_REQUEST_EXTRACTED);
    }
    csvServiceUtils.moveCsvFile(bucketName, "CE");
  }
}
