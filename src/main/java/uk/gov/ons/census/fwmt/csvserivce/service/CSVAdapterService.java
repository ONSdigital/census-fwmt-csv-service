package uk.gov.ons.census.fwmt.csvserivce.service;

import uk.gov.ons.census.fwmt.common.error.GatewayException;

public interface CSVAdapterService {

  // TODO : Create object in common
  void sendJobRequest(String objectToSendToQueue) throws GatewayException;

}
