package uk.gov.ons.census.fwmt.csvservice.service;

public interface CSVConverterService {
  void convertCSVToCanonical(String ingestType) throws Exception;
}
