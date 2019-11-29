package uk.gov.ons.census.fwmt.csvservice.service;

import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

public interface CSVAdapterService {
  void sendJobRequest(CreateFieldWorkerJobRequest createFieldWorkerJobRequest, String event) throws GatewayException;
}
