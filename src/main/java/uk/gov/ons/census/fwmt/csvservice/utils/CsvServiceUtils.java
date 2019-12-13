package uk.gov.ons.census.fwmt.csvservice.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.sql.Timestamp;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

@Slf4j
public final class CsvServiceUtils {

  public static void moveCsvFile(Resource csvGCPFile, Path csvPath, Path processedPath) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String timeStamp = String.valueOf(timestamp.toInstant());
    String originalFileName = csvGCPFile.getFilename();

    Path resolveSibling = processedPath.resolveSibling("processed-" + timeStamp + ".csv");

    if (originalFileName != null) {

      try (OutputStream os = ((WritableResource) resolveSibling).getOutputStream()) {

        StreamUtils.copy(csvGCPFile.getInputStream(), os);

        // Files.move(csvPath, processedPath.resolveSibling("processed-" +
        // timeStamp + ".csv"), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to move/rename ingest CSV");
      }
    }
  }

  public static void moveCsvFile(Resource csvGCPFile2, Resource processedPath2) throws  IOException {
    log.info(processedPath2.getFilename());
    try (OutputStream os = ((WritableResource) processedPath2).getOutputStream()) {

      StreamUtils.copy(csvGCPFile2.getInputStream(), os);
    }
  }
}
