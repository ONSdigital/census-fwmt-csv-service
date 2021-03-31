package uk.gov.ons.census.fwmt.csvservice.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.csvservice.dto.GatewayCache;

import javax.transaction.Transactional;

@Component
public class DatabaseLookup {

  @Autowired
  private GatewayCacheService gatewayCacheService;

  @Transactional
  public GatewayCache checkIfCaseExists(String caseId) {
    GatewayCache gatewayCache = gatewayCacheService.getById(caseId);
    return gatewayCache;
  }
}
