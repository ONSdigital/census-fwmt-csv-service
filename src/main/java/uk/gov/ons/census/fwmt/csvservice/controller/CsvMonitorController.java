package uk.gov.ons.census.fwmt.csvservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;

@Controller
public class CsvMonitorController {

  @Autowired
  private CSVConverterService csvConverterServiceImpl;


  @GetMapping("/ingestCeCsvFile")
  public ResponseEntity ingestCeCsvFile() throws GatewayException {
    csvConverterServiceImpl.convertCSVToCanonical(CSVConverterService.CE_INGEST);
    return ResponseEntity.ok("CSV adapter service activated");
  }

  @GetMapping("/ingestCCSCsvFile")
  public ResponseEntity ingestCSSCsvFile() throws GatewayException {
    csvConverterServiceImpl.convertCSVToCanonical(CSVConverterService.CCS_INGEST);
    return ResponseEntity.ok("CSV adapter service activated");
  }
}