package uk.gov.ons.census.fwmt.csvservice.service;

import uk.gov.ons.census.fwmt.common.error.GatewayException;

public interface CSVConverterService {
  public static final String CCS_INGEST = "CCSIngest";

  public static final String CE_INGEST = "CEIngest";

  void convertCSVToCanonical(String ingestType) throws GatewayException;
}
