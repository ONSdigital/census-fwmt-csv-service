package uk.gov.ons.census.fwmt.csvservice.service.ccs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class CCSGatewayEventsConfig {

  public static final String CSV_CCS_REQUEST_EXTRACTED = "CSV_CCS_REQUEST_EXTRACTED";
  public static final String CANONICAL_CCS_CREATE_SENT = "CANONICAL_CCS_CREATE_SENT";

  @Bean
  public void addCEEvents(GatewayEventManager gatewayEventManager) {
    gatewayEventManager.addEventTypes(new String[] {CSV_CCS_REQUEST_EXTRACTED, CANONICAL_CCS_CREATE_SENT});
  }
}
