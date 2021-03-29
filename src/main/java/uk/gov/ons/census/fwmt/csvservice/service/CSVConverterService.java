package uk.gov.ons.census.fwmt.csvservice.service;

import uk.gov.ons.census.fwmt.common.error.GatewayException;

public interface CSVConverterService {
  void convertToCanonical() throws GatewayException;
}
