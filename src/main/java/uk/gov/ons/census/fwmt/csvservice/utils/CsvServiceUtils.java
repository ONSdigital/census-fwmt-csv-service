package uk.gov.ons.census.fwmt.csvservice.utils;

import org.springframework.core.io.Resource;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;

public final class CsvServiceUtils {

  public static void moveCsvFile(Resource csvGCPFile, Path csvPath, Path processedPath) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Instant instant = timestamp.toInstant();
    String originalFileName = csvGCPFile.getFilename();

    if (originalFileName != null) {
      try {
        Files.move(csvPath, processedPath);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to move ingest CSV");
      }
    }
  }
}
