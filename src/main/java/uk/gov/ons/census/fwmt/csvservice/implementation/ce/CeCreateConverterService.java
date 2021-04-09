package uk.gov.ons.census.fwmt.csvservice.implementation.ce;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.CeCreate;
import uk.gov.ons.census.fwmt.csvservice.dto.GatewayCache;
import uk.gov.ons.census.fwmt.csvservice.implementation.DatabaseLookup;
import uk.gov.ons.census.fwmt.csvservice.message.RmFieldRepublishProducer;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component("CE")
public class CeCreateConverterService implements CSVConverterService {

  public static final String CSV_CE_CREATE_REQUEST_EXTRACTED = "CSV_NON_COMPLIANCE_REQUEST_EXTRACTED";

  public static final String CANONICAL_CE_CREATE_SENT = "CANONICAL_NON_COMPLIANCE_CREATE_SENT";

  public static final String CSV_CE_CREATE_EXISTS_IN_CACHE = "CSV_CE_CREATE_EXISTS_IN_CACHE";

  public static final String CSV_CE_CREATE_TERMINATING_INGEST = "CSV_CE_CREATE_TERMINATING_INGEST";

  @Value("${gcpBucket.directory}")
  private String directory;

  @Autowired
  private RmFieldRepublishProducer rmFieldRepublishProducer;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  @Autowired
  private DatabaseLookup databaseLookup;

  private final List<String> errorList = new ArrayList<>();

  @Override
  public void convertToCanonical() throws GatewayException {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime now = LocalDateTime.now();
    String timestamp = dateTimeFormatter.format(now);
    List<URI> ceCreateFiles = storageUtils.getFilenamesInFolder(URI.create(directory), "CECREATE");

    for (URI uri : ceCreateFiles) {
      InputStream inputStream = storageUtils.getFileInputStream(uri);
      List<CeCreate> ceCreateList = createCsvBean(inputStream);
      validateObject(ceCreateList);
      if (!errorList.isEmpty()) {
        gatewayEventManager.triggerErrorEvent(this.getClass(), "Terminating CSV load", "NA",
            CSV_CE_CREATE_TERMINATING_INGEST, "Cases", errorList.toString());
        storageUtils.move(uri, URI.create(directory + "/processed/" + "CE-Create-processed-invalid" + timestamp));
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, "Found a case within cache");
      }
      processObject(ceCreateList);
      storageUtils.move(uri, URI.create(directory + "/processed/" + "CE-Create-processed-" + timestamp));
    }
  }

  private void validateObject(List<CeCreate> ceCreateList) {
    errorList.clear();
    for (CeCreate ceCreate : ceCreateList) {
      GatewayCache cache = databaseLookup.getCaseFromCache(ceCreate.getCaseId());
      if (cache != null && cache.existsInFwmt) {
        errorList.add(ceCreate.getCaseId());
      }
    }
  }

  private List<CeCreate> createCsvBean(InputStream inputStream) {
    CsvToBean<CeCreate> csvToBean;
    csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .withSeparator('|')
        .withType(CeCreate.class)
        .build();

    return csvToBean.parse();
  }

  private void processObject(List<CeCreate> ceCreateList) {
    for (CeCreate ceCreate : ceCreateList) {
      gatewayEventManager.triggerEvent(String.valueOf(ceCreate.getCaseId()), CSV_CE_CREATE_REQUEST_EXTRACTED);
      createAndSendJob(ceCreate);
    }
  }

  private void createAndSendJob(CeCreate ceCreate) {
    FwmtActionInstruction fwmtActionInstruction = CeCreateBuilder.buildCeCreate(ceCreate);
    rmFieldRepublishProducer.republish(fwmtActionInstruction);
    gatewayEventManager.triggerEvent(String.valueOf(fwmtActionInstruction.getCaseId()), CANONICAL_CE_CREATE_SENT,
        "Case reference", fwmtActionInstruction.getCaseRef());
  }
}

