package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSCanonicalBuilder.createCCSJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CANONICAL_CCS_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;

@Component("CCS")
public class CCSConverterService implements CSVConverterService {

  @Value("${gcpBucket.ccslocation}")
  private Resource file;

  @Value("${gcpBucket.directory}")
  private String directory;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  @Override
  public void convertToCanonical() throws GatewayException {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = dateTimeFormatter.format(now);
    CsvToBean<CCSPropertyListing> csvToBean;
    try {
      InputStream inputStream = storageUtils.getFileInputStream(file.getURI());
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
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
    try {
      storageUtils.move(file.getURI(), URI.create(directory + "/processed/" + "CE-processed-" + timestamp));
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed to move file");
    }
  }
}
