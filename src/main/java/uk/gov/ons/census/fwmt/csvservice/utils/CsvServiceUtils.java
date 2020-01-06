package uk.gov.ons.census.fwmt.csvservice.utils;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Slf4j
@Component
public class CsvServiceUtils {

  @Autowired
  private Storage googleCloudStorage;

  public void moveCsvFile(String bucketName, String prefix) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    Bucket startBucket = googleCloudStorage.get(bucketName);
    Page<Blob> blobPage = startBucket.list(Storage.BlobListOption.prefix(prefix));

    for (Blob blob : blobPage.iterateAll()) {
      copyAndRename(startBucket, blob, timestamp, prefix);
    }
  }

  private void copyAndRename(Bucket startBucket, Blob blob, Timestamp timeStamp, String prefix) {
    BlobId source = BlobId.of(startBucket.getName(), blob.getName());
    BlobId target = BlobId
        .of(startBucket.getName(), "processed/" + prefix + "-processed-" + timeStamp + "-Orig-" + blob.getName());
    Storage.CopyRequest copyRequest = Storage.CopyRequest.of(source, target);
    this.googleCloudStorage.copy(copyRequest);
    this.googleCloudStorage.delete(source);
  }
}
