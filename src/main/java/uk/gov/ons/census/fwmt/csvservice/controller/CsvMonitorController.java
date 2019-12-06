package uk.gov.ons.census.fwmt.csvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSConverterService;
import uk.gov.ons.census.fwmt.csvservice.implementation.ce.CEConverterService;

@Controller
public class CsvMonitorController {

  @Autowired
  private CCSConverterService ccsConverterService;

  @Autowired
  private CEConverterService ceConverterService;

  @GetMapping("/ingestCeCsvFile")
  public ResponseEntity<String> ingestCeCsvFile() throws GatewayException {
    ceConverterService.convertToCanonical();
    return ResponseEntity.ok("CSV adapter service activated");
  }

  @GetMapping("/ingestCCSCsvFile")
  public ResponseEntity<String> ingestCSSCsvFile() throws GatewayException {
    ccsConverterService.convertToCanonical();
    return ResponseEntity.ok("CSV adapter service activated");
  }
}