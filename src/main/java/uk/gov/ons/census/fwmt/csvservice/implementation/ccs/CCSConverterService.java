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
import java.util.List;
import java.util.Scanner;

import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSCanonicalBuilder.createCCSJob;

@Component("CCS")
public class CCSConverterService implements CSVConverterService {

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

  public static final String CSV_CCS_REQUEST_EXTRACTED = "CSV_CCS_REQUEST_EXTRACTED";

  public static final String CANONICAL_CCS_CREATE_SENT = "CANONICAL_CCS_CREATE_SENT";

  @Override
  public void convertToCanonical() throws GatewayException {
    String caseRefFileContents;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = dateTimeFormatter.format(now);
    List<URI> ccsPropertyListingFiles = storageUtils.getFilenamesInFolder(URI.create(directory), "ccs");

    try {
      InputStream inputStream = storageUtils.getFileInputStream(caseRefCountFile.getURI());
      try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
        caseRefFileContents = scanner.useDelimiter("\\A").next();
      }
    } catch (IOException e) {
      String msg = "Failed to convert inputStream.";
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    int caseRefCount = Integer.parseInt(caseRefFileContents.trim());

    CsvToBean<CCSPropertyListing> csvToBean;
    for (URI uri : ccsPropertyListingFiles) {
      InputStream inputStream = storageUtils.getFileInputStream(uri);
      csvToBean = createCsvBean(inputStream);

      caseRefCount = processObject(csvToBean, caseRefCount);
      storageUtils.move(uri, URI.create(directory + "/processed/" + "CCSPL-processed-" + timestamp));
    }
    updateCaseRefCout(caseRefCount);
  }

  private CsvToBean<CCSPropertyListing> createCsvBean(InputStream inputStream) {
    CsvToBean<CCSPropertyListing> csvToBean;
    csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .withSeparator(',')
        .withType(CCSPropertyListing.class)
        .build();

    return csvToBean;
  }

  private int processObject(CsvToBean<CCSPropertyListing> csvToBean, int caseRefCount) {
    for (CCSPropertyListing ccsPropertyListing : csvToBean) {
      createAndSendJob(ccsPropertyListing, caseRefCount);
      caseRefCount++;
    }
    return caseRefCount;
  }

  private void createAndSendJob(CCSPropertyListing ccsPropertyListing, int caseRefCount) {
    FwmtActionInstruction fwmtActionInstruction = createCCSJob(ccsPropertyListing, caseRefCount);
    gatewayEventManager.triggerEvent(String.valueOf(fwmtActionInstruction.getCaseId()),
        CSV_CCS_REQUEST_EXTRACTED);
    rmFieldRepublishProducer.republish(fwmtActionInstruction);
    gatewayEventManager.triggerEvent(String.valueOf(fwmtActionInstruction.getCaseId()),
        CANONICAL_CCS_CREATE_SENT);
  }

  private void updateCaseRefCout(int caseRefCount) throws GatewayException {
    File file;
    try {
      file = File.createTempFile("caseRefCount", ".txt");
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }

    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
      writeFile(caseRefCount, writer);
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }
    String filename = "caseRefCount.txt";
    storageUtils.move(file.toURI(), URI.create(directory + filename));
  }

  private void writeFile(int caseRefCount, Writer writer) throws GatewayException {
    try {
      writer.write(String.valueOf(caseRefCount));
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed write temp file");
    }
  }
}
