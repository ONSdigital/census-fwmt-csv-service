package uk.gov.ons.census.fwmt.csvservice.implementation.ce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Component("CE")
public class CEConverterService implements CSVConverterService {

  @Value("${gcpBucket.celocation}")
  private Resource file;

  @Value("${gcpBucket.directory}")
  private String directory;

//  @Autowired
//  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  @Override
  public void convertToCanonical() throws GatewayException {
//    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//    LocalDateTime now = LocalDateTime.now();
//    String timestamp = dateTimeFormatter.format(now);
//    CsvToBean<CEJobListing> csvToBean;
//    try {
//      InputStream inputStream = storageUtils.getFileInputStream(file.getURI());
//      csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
//          .withType(CEJobListing.class)
//          .build();
//    } catch (IOException e) {
//      String msg = "Failed to convert CSV to Bean.";
//      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
//      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
//    }
//
//    for (CEJobListing CEJobListing : csvToBean) {
//      CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createCEJob(CEJobListing);
//      gatewayActionAdapter.sendJobRequest(createFieldWorkerJobRequest, CANONICAL_CE_CREATE_SENT);
//      gatewayEventManager
//          .triggerEvent(String.valueOf(createFieldWorkerJobRequest.getCaseId()), CSV_CE_REQUEST_EXTRACTED);
//    }
//    try {
//      storageUtils.move(file.getURI(), URI.create(directory + "/processed/" + "CE-processed-" + timestamp));
//    } catch (IOException e) {
//      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed to move file");
//    }
  }
}
