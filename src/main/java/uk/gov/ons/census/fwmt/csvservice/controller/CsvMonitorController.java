package uk.gov.ons.census.fwmt.csvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;

import java.util.Map;

@Controller
public class CsvMonitorController {

  @Autowired
  private Map<String, CSVConverterService> serviceMap;

  @GetMapping("/ingestCeCsvFile")
  public ResponseEntity<String> ingestCeCsvFile() throws GatewayException {
    serviceMap.get("CE").convertToCanonical();
    return ResponseEntity.ok("CSV adapter service activated");
  }

  @GetMapping("/ingestCCSCsvFile")
  public ResponseEntity<String> ingestCSSCsvFile() throws GatewayException {
    serviceMap.get("CCS").convertToCanonical();
    return ResponseEntity.ok("CSV adapter service activated");
  }
}