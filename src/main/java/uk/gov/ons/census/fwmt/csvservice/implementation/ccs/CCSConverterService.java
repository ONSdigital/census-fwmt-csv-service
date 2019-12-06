package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

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
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSCanonicalBuilder.createCCSJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CANONICAL_CCS_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;
import static uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils.moveCsvFile;

@Component("CCS")
public class CCSConverterService implements CSVConverterService {

  @Value("${gcpBucket.ccslocation}")
  private Resource csvGCPFile;

  @Value("${gcpBucket.ccslocation}")
  private Path csvPath;

  @Value("${gcpBucket.ccsProcessedPath}")
  private Path processedPath;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Override
  public void convertToCanonical() throws GatewayException {
    CsvToBean<CCSPropertyListing> csvToBean;
    try {
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(csvGCPFile.getInputStream(), StandardCharsets.UTF_8))
          .withType(CCSPropertyListing.class)
          .build();

    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    for (CCSPropertyListing ccsPropertyListing : csvToBean) {
      CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createCCSJob(ccsPropertyListing);
      gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_CCS_CREATE_SENT);
      gatewayEventManager
          .triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()), CSV_CCS_REQUEST_EXTRACTED);
    }
    moveCsvFile(csvGCPFile, csvPath, processedPath);
  }
}
