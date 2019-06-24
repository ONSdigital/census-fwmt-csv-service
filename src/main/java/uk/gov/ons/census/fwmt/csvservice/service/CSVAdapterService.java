package uk.gov.ons.census.fwmt.csvservice.service;

import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

public interface CSVAdapterService {

  // TODO : Create object in common
  void sendJobRequest(CreateFieldWorkerJobRequest createFieldWorkerJobRequest) throws GatewayException;

}
