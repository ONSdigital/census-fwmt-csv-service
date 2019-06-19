package uk.gov.ons.census.fwmt.csvserivce.service;

import uk.gov.ons.census.fwmt.common.error.GatewayException;

public interface CSVConverterService {

  // TODO : implement method - read in csv possibly used for transform in reading from GoAnywhere
  void convertCSVToObject() throws GatewayException;

}
