package uk.gov.ons.census.fwmt.csvservice.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.Storage;
import org.checkerframework.checker.units.qual.A;
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
  private Storage gcs;

  private static final String SEPARATOR = "/";

  public void moveCsvFile(Resource csvGCPFile, Resource processedPath) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String timeStamp = String.valueOf(timestamp.toInstant());
    String originalFileName = csvGCPFile.getFilename();

    String outputPath = null;
    try {
      outputPath = processedPath.getURI().toString() + originalFileName + "processed-" + timeStamp + ".csv";
    } catch (IOException e) {
      e.printStackTrace();
    }

    String[] fromTokens = new String[0];
    try {
      fromTokens = getBucketAndObjectFromPath(String.valueOf(csvGCPFile.getURI()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert outputPath != null;
    String[] toTokens = getBucketAndObjectFromPath(outputPath);

    // There is currently no way to rename/move things in GCS, so we'll have to copy and remove.
    BlobId source = BlobId.of(fromTokens[0], fromTokens[1]);
    BlobId target = BlobId.of(toTokens[0], toTokens[1]);
    Storage.CopyRequest copyRequest = Storage.CopyRequest.of(source, target);
    this.gcs.copy(copyRequest);
    this.gcs.delete(source);
  }

  private String[] getBucketAndObjectFromPath(String path) {
    return path.split(SEPARATOR, 2);
  }

}
