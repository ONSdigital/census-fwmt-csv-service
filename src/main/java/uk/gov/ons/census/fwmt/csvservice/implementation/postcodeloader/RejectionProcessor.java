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
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.dto.RejectionReport;
import uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader.LookupFileLoaderEventsConfig.CREATED_REJECTION_FILE;

@Slf4j
@Component
public class RejectionProcessor {

  @Value("${gcpBucket.rejectLocation}")
  String location;
  @Autowired
  private CsvServiceUtils csvServiceUtils;
  @Autowired
  private GatewayEventManager gatewayEventManager;

  public void createErrorReports(List<AddressCheckListing> rejectionsList,
      List<RejectionReport> rejectedReportList) throws GatewayException {
    String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
    createRejectionCsv(rejectionsList, timeStamp);
    createRejectionReport(rejectedReportList, timeStamp);
  }

  private void createRejectionReport(
      List<RejectionReport> rejectedReportList, String timeStamp) throws GatewayException {
    File file;
    try {
      file = File.createTempFile("rejectionTemp", ".report");
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }

    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8);
        InputStream inputStream = new FileInputStream(file)) {

      for (RejectionReport rejectionReport : rejectedReportList) {
        try {
          writer.write("Case Reference: " + rejectionReport.getCaseRef() + " | " + rejectionReport.getReason());
          writer.write("\n");
        } catch (IOException e) {
          throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
        }
      }

      String filename = "reject_" + timeStamp + ".txt";

      csvServiceUtils.uploadFile(inputStream, filename, location);
      gatewayEventManager.triggerEvent(filename, CREATED_REJECTION_FILE);
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }
    file.deleteOnExit();
  }

  private void createRejectionCsv(List<AddressCheckListing> rejectionsList, String timeStamp) throws GatewayException {
    File file;
    try {
      file = File.createTempFile("rejectionTemp", ".csv");
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed creating temp file to write to.");
    }

    try (Writer writer = new FileWriter(file.getAbsolutePath(), StandardCharsets.UTF_8);
        InputStream inputStream = new FileInputStream(file)) {
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

      String filename = "reject_" + timeStamp + ".csv";

      csvServiceUtils.uploadFile(inputStream, filename, location);
      gatewayEventManager.triggerEvent(filename, CREATED_REJECTION_FILE);

    } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Failed writing CSV to GCP");
    }
    file.deleteOnExit();
  }
}
