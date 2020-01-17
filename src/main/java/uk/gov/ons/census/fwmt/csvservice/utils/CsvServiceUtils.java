package uk.gov.ons.census.fwmt.csvservice.utils;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

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

  public void uploadFile(InputStream is, String filename, String location) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    byte[] readBuf = new byte[4096];
    while (is.available() > 0) {
      int bytesRead = is.read(readBuf);
      os.write(readBuf, 0, bytesRead);
    }

    String bucket = location.substring(location.indexOf(":") + 1);
    bucket = bucket.trim().replaceAll("/", "");
    googleCloudStorage.create(
        BlobInfo
            .newBuilder(bucket, filename)
            .setAcl(new ArrayList<>(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
            .build(),
        os.toByteArray());
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
