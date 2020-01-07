package uk.gov.ons.census.fwmt.csvservice.service;

import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.data.PostcodeLookup;

import java.util.Map;

public interface LookupFileLoaderService {

  void loadPostcodeLookupFile() throws GatewayException;

  Map<String, PostcodeLookup> getLookupMap();

}
