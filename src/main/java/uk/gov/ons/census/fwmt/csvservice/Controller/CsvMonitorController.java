package uk.gov.ons.census.fwmt.csvservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Controller
public class CsvMonitorController {

  @Autowired
  private CSVConverterService csvConverterService;

  @GetMapping("/enableCsvService")
  public ResponseEntity enableCsvService()
      throws Exception {
    csvConverterService.convertCSVToObject();
    return ResponseEntity.ok("CSV adapter service activated");
  }

}