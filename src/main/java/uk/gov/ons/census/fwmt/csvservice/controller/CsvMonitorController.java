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
  private Map<String, CSVConverterService> csvServiceMap;

  @GetMapping("/ingestCCSFile")
  public ResponseEntity<String> ingestCSSCsvFile() throws GatewayException {
    final CSVConverterService ccsConverterService = csvServiceMap.get("CCS");
    ccsConverterService.convertToCanonical();
    return ResponseEntity.ok("CCS adapter service activated");
  }

  @GetMapping("/ingestNonComplianceFile")
  public ResponseEntity<String> ingestNCCsvFile() throws GatewayException {
    final CSVConverterService ncConverterService = csvServiceMap.get("NC");
    ncConverterService.convertToCanonical();
    return ResponseEntity.ok("Non Compliance adapter service activated");
  }

}