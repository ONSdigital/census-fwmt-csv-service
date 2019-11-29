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
  public ResponseEntity ingestCeCsvFile() throws GatewayException {
    serviceMap.get("CE").convertCSVToCanonical();
    return ResponseEntity.ok("CSV adapter service activated");
  }

  @GetMapping("/ingestCCSCsvFile")
  public ResponseEntity ingestCSSCsvFile() throws GatewayException {
    serviceMap.get("CCS").convertCSVToCanonical();
    return ResponseEntity.ok("CSV adapter service activated");
  }
}