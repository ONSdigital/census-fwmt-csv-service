package uk.gov.ons.census.fwmt.csvservice.service.ce;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.CEJobListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static uk.gov.ons.census.fwmt.csvservice.service.ce.CECanonicalBuilder.createCEJob;
import static uk.gov.ons.census.fwmt.csvservice.service.ce.CEGatewayEventsConfig.CANONICAL_CE_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.service.ce.CEGatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;
import static uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils.moveCsvFile;

@Slf4j
@Service("CE")
public class CEConverterServiceImpl implements CSVConverterService {

  @Value("${gcpBucket.celocation}")
  private Resource csvGCPFile;

  @Value("${gcpBucket.celocation}")
  private Path csvPath;

  @Value("${gcpBucket.ceProcessedPath}")
  private Path processedPath;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Override
  public void convertCSVToCanonical() throws GatewayException {
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
    moveCsvFile(csvGCPFile, csvPath, processedPath);
  }
}
