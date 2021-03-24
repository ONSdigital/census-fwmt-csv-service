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
import uk.gov.ons.census.fwmt.csvservice.message.RmFieldRepublishProducer;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component("CE")
public class CeCreateConverterService implements CSVConverterService {

  public static final String CSV_NON_COMPLIANCE_REQUEST_EXTRACTED = "CSV_NON_COMPLIANCE_REQUEST_EXTRACTED";

  public static final String CANONICAL_NON_COMPLIANCE_CREATE_SENT = "CANONICAL_NON_COMPLIANCE_CREATE_SENT";

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
    List<URI> ceCreateFiles = storageUtils.getFilenamesInFolder(URI.create(directory), "CECREATE");

    CsvToBean<CeCreate> csvToBean;
    for (URI uri : ceCreateFiles) {
      InputStream inputStream = storageUtils.getFileInputStream(uri);
      csvToBean = createCsvBean(inputStream);

      processObject(csvToBean);
      storageUtils.move(uri, URI.create(directory + "/processed/" + "CE-Create-processed-" + timestamp));
    }
  }

  private CsvToBean<CeCreate> createCsvBean(InputStream inputStream) {
    CsvToBean<CeCreate> csvToBean;
    csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .withSeparator('|')
        .withType(CeCreate.class)
        .build();

    return csvToBean;
  }

  private void processObject(CsvToBean<CeCreate> csvToBean) {
    for (CeCreate ceCreate : csvToBean) {
      gatewayEventManager.triggerEvent(String.valueOf(ceCreate.getCaseId()), CSV_NON_COMPLIANCE_REQUEST_EXTRACTED);
      createAndSendJob(ceCreate);
    }
  }

  private void createAndSendJob(CeCreate ceCreate) {
    FwmtActionInstruction fwmtActionInstruction = CeCreateBuilder.buildCeCreate(ceCreate);
    rmFieldRepublishProducer.republish(fwmtActionInstruction);
    gatewayEventManager.triggerEvent(String.valueOf(fwmtActionInstruction.getCaseId()), CANONICAL_NON_COMPLIANCE_CREATE_SENT,
        "Case reference", fwmtActionInstruction.getCaseRef());
  }
}

