package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSCanonicalBuilder.createCCSJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CANONICAL_CCS_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;

@Component("CCS")
public class CCSConverterService implements CSVConverterService {

  @Value("${gcpBucket.ccslocation}")
  private Resource csvGCPFile;

  @Value("${gcpBucket.ccsProcessedPath}")
  private Resource processedPath;

  @Autowired
  private GatewayActionAdapter gatewayActionAdapter;

  @Autowired
  private GatewayEventManager gatewayEventManager;
  
  @Autowired
  private DefaultResourceLoader defaultResourceLoader;

  @Override
  public void convertToCanonical() throws GatewayException {
    Resource fileR = defaultResourceLoader.getResource("file://csv-bucket-test/ccsTestCSV.csv");
    Resource gR = defaultResourceLoader.getResource("gs://csv-bucket-test/processed/ccsTestCSV.csv");
    
    
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

    try {
      moveCsvFile(csvGCPFile, Paths.get(csvGCPFile.getURI()), processedPath);
      //moveCsvFile(csvGCPFile, processedPath);
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed to read path");
    }
  }

  public void moveCsvFile(Resource csvGCPFile, Path csvPath, Resource processedPath) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String timeStamp = String.valueOf(timestamp.toInstant());
    String originalFileName = csvGCPFile.getFilename();

    String outPath = "";
    try {
      outPath = processedPath.getURI().toString()+ originalFileName+ "processed-" + timeStamp + ".csv";
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    Resource outResource = defaultResourceLoader.getResource(outPath);

    if (originalFileName != null) {

      try (OutputStream os = ((WritableResource) outResource).getOutputStream()) {

        StreamUtils.copy(csvGCPFile.getInputStream(), os);

        // Files.move(csvPath, processedPath.resolveSibling("processed-" +
        // timeStamp + ".csv"), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to move/rename ingest CSV");
      }
    }
  }


}
