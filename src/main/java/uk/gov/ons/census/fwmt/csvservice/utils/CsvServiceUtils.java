package uk.gov.ons.census.fwmt.csvservice.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

@Slf4j
@Component
public class CsvServiceUtils {

  @Autowired
  private DefaultResourceLoader defaultResourceLoader;

  public void moveCsvFile(Resource csvGCPFile, Resource processedPath) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String timeStamp = String.valueOf(timestamp.toInstant());
    String originalFileName = csvGCPFile.getFilename();

    String outputPath = "Failed to read path{} ingest CSV";
    try {
      outputPath = processedPath.getURI().toString() + originalFileName + "processed-" + timeStamp + ".csv";
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, outputPath, processedPath);
    }

    Resource outResource = defaultResourceLoader.getResource(outputPath);

    if (originalFileName != null) {
      try (OutputStream os = ((WritableResource) outResource).getOutputStream()) {
        StreamUtils.copy(csvGCPFile.getInputStream(), os);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed to copy ingest CSV");
      }

      try {
        Files.delete((Path) csvGCPFile);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed to delete processed ingest CSV");
      }
    }
  }
}
