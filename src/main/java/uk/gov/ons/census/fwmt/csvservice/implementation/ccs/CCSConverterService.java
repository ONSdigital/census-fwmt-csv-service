package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.message.RmFieldRepublishProducer;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSCanonicalBuilder.createCCSJob;

@Component("CCS")
public class CCSConverterService implements CSVConverterService {

  public static final String CSV_CCS_REQUEST_EXTRACTED = "CSV_CCS_REQUEST_EXTRACTED";

  @Value("${gcpBucket.ccslocation}")
  private Resource propertyListingFile;

  @Value("${gcpBucket.casereflocation}")
  private Resource caseRefCountFile;

  @Value("${gcpBucket.directory}")
  private String directory;

  @Autowired
  private RmFieldRepublishProducer rmFieldRepublishProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  @Override
  public void convertToCanonical() throws GatewayException {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = dateTimeFormatter.format(now);
    CsvToBean<CCSPropertyListing> csvToBean;
    int caseRefCount = 0;
    try {
      InputStream inputStream = storageUtils.getFileInputStream(propertyListingFile.getURI());
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
          .withType(CCSPropertyListing.class)
          .build();

    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    try {
      InputStream caseRefCountInputStream = storageUtils.getFileInputStream(caseRefCountFile.getURI());
      caseRefCount = Integer.parseInt(caseRefCountInputStream.toString());
    } catch (IOException e) {
      String msg = "Failed to convert inputStream.";
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    for (CCSPropertyListing ccsPropertyListing : csvToBean) {
      FwmtActionInstruction fwmtActionInstruction = createCCSJob(ccsPropertyListing, caseRefCount);
      rmFieldRepublishProducer.republish(fwmtActionInstruction);
      gatewayEventManager
          .triggerEvent(String.valueOf(fwmtActionInstruction.getCaseId()), CSV_CCS_REQUEST_EXTRACTED);
      caseRefCount++;
    }

    try {
      storageUtils.move(propertyListingFile.getURI(), URI.create(directory + "/processed/" + "CCS-processed-" + timestamp));
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed to move file");
    }

    File file;
    try {
      file = File.createTempFile("caseRefCount", ".txt");
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }

    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
        try {
          writer.write(caseRefCount);
        } catch (IOException e) {
          throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed write temp file");
        }
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }
    String filename = "caseRefCount.txt";
    storageUtils.move(file.toURI(), URI.create(directory + filename));
  }
}
