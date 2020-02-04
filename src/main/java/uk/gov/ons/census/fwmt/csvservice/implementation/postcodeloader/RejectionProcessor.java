package uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.dto.RejectionReportEntry;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader.LookupFileLoaderEventsConfig.CREATED_REJECTION_FILE;

@Slf4j
@Component
public class RejectionProcessor {

  @Value("${gcpBucket.directory}")
  private String directory;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  public void createErrorReports(List<AddressCheckListing> rejectionsList,
      List<RejectionReportEntry> rejectedReportList) throws GatewayException {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    createRejectionCsv(rejectionsList, timestamp);
    createRejectionReport(rejectedReportList, timestamp);
  }

  private void createRejectionReport(
      List<RejectionReportEntry> rejectedReportList, String timeStamp) throws GatewayException {
    File file;
    try {
      file = File.createTempFile("rejectionTemp", ".txt");
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }

    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
      for (RejectionReportEntry rejectionReportEntry : rejectedReportList) {
        try {
          writer
              .write("Case Reference: " + rejectionReportEntry.getCaseRef() + " | " + rejectionReportEntry.getReason());
          writer.write("\n");
        } catch (IOException e) {
          throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed write temp file");
        }
      }
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }
    String filename = "reject_" + timeStamp + ".txt";
    storageUtils.move(file.toURI(), URI.create(directory + filename));
    gatewayEventManager.triggerEvent(filename, CREATED_REJECTION_FILE);
    file.deleteOnExit();
  }

  private void createRejectionCsv(List<AddressCheckListing> rejectionsList, String timeStamp) throws GatewayException {
    File file;
    try {
      file = File.createTempFile("rejectionTemp", ".csv");
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }

    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8)) {
      writer.write(
          "caseReference|GuidancePrompt|line1|line2|line3|townName|postCode|latitude|longitude|additionalInformation\n");

      ColumnPositionMappingStrategy<AddressCheckListing> mappingStrategy = new ColumnPositionMappingStrategy<>();
      mappingStrategy.setType(AddressCheckListing.class);
      String[] columns = new String[] {"caseReference", "GuidancePrompt", "line1", "line2", "line3", "townName",
          "postCode", "latitude", "longitude", "additionalInformation"};
      mappingStrategy.setColumnMapping(columns);

      StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer)
          .withSeparator('|')
          .withMappingStrategy(mappingStrategy)
          .withApplyQuotesToAll(false)
          .build();

      sbc.write(rejectionsList);
    } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed writing temp file");
    }
    String filename = "reject_" + timeStamp + ".csv";
    storageUtils.move(file.toURI(), URI.create(directory + filename));
    gatewayEventManager.triggerEvent(filename, CREATED_REJECTION_FILE);
    file.deleteOnExit();
  }
}
