package uk.gov.ons.census.fwmt.csvservice.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;

public final class CsvServiceUtils {

  public static void moveCsvFile(Resource csvGCPFile, Path csvPath, Path processedPath) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Instant instant = timestamp.toInstant();
    String csvFileExtension = csvGCPFile.getFilename();

    if (csvFileExtension != null) {
      String csvData = "Unable to read CSV data";
      try {
        csvData = Files.readString(csvPath, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, csvData);
      }
      csvFileExtension = csvFileExtension.replace(".csv", ".processed-" + instant);
      final String finalPath = processedPath + csvFileExtension;

      try (OutputStream os = ((WritableResource) csvGCPFile.createRelative(finalPath)).getOutputStream()) {
        os.write(csvData.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to rename Ingest CSV");
      }
      try {
        Files.delete(csvPath);
      } catch (IOException e) {
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to delete Ingest CSV");
      }
    }
  }
}
