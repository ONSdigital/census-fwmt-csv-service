package uk.gov.ons.census.fwmt.csvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.dto.PostcodeLookup;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.csvservice.service.LookupFileLoaderService;

import java.util.Map;

@Controller
public class CsvMonitorController {

  @Autowired
  private Map<String, CSVConverterService> csvServiceMap;

  @Autowired
  private LookupFileLoaderService lookupFileLoaderService;

  @GetMapping("/ingestCeCsvFile")
  public ResponseEntity<String> ingestCeCsvFile() throws GatewayException {
    final CSVConverterService ceConverterService = csvServiceMap.get("CE");
    ceConverterService.convertToCanonical();
    return ResponseEntity.ok("CE adapter service activated");
  }

  @GetMapping("/ingestCCSCsvFile")
  public ResponseEntity<String> ingestCSSCsvFile() throws GatewayException {
    final CSVConverterService ccsConverterService = csvServiceMap.get("CCS");
    ccsConverterService.convertToCanonical();
    return ResponseEntity.ok("CCS adapter service activated");
  }

  @GetMapping("/ingestAddressCheckCsvFile")
  public ResponseEntity<String> ingestAddressCheckCsvFile() throws GatewayException {
    final CSVConverterService ccsConverterService = csvServiceMap.get("AC");
    ccsConverterService.convertToCanonical();
    return ResponseEntity.ok("AC adapter service activated");
  }

  @GetMapping("/ingestAddressLookupCsvFile")
  public ResponseEntity<String> loadAddressLookupFile() throws GatewayException {
    lookupFileLoaderService.loadPostcodeLookupFile();
    return ResponseEntity.ok("Address Lookup file loaded");
  }

  @GetMapping("/getPostcodeLookupMap")
  public ResponseEntity<Map<String, PostcodeLookup>> getPostcodeLookupMap() {
    return ResponseEntity.ok(lookupFileLoaderService.getLookupMap());
  }

  @GetMapping("/getPostcodeLookup")
  public ResponseEntity<PostcodeLookup> getPostcodeLookup(@RequestParam String key) {
    return ResponseEntity.ok(lookupFileLoaderService.getPostcodeLookup(key));
  }

}