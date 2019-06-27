package uk.gov.ons.census.fwmt.csvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ons.census.fwmt.csvservice.service.impl.CSVConverterServiceImpl;

@Controller
public class CsvMonitorController {

  @Autowired
  private CSVConverterServiceImpl csvConverterServiceImpl;

  @GetMapping("/enableCsvService")
  public ResponseEntity enableCsvService()
      throws Exception {
    csvConverterServiceImpl.convertCSVToObject();
    return ResponseEntity.ok("CSV adapter service activated");
  }
}