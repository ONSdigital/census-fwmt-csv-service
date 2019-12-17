package uk.gov.ons.census.fwmt.csvservice.utils;

import java.sql.Timestamp;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

@Slf4j
@Component
public class CsvServiceUtils {

  @Autowired
  private Storage googleCloudStorage;

  public void moveCsvFile(String bucketName, String blobName) throws GatewayException {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String timeStamp = String.valueOf(timestamp.toInstant());

    // There is currently no way to rename/move things in GCS, so we'll have to copy and remove.
    BlobId source = BlobId.of(bucketName, blobName);
    BlobId target = BlobId.of(bucketName, "processed/" + timeStamp + blobName);
    Storage.CopyRequest copyRequest = Storage.CopyRequest.of(source, target);
    this.googleCloudStorage.copy(copyRequest);
    this.googleCloudStorage.delete(source);
  }
}
