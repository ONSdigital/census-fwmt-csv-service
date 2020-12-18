package uk.gov.ons.census.fwmt.csvservice.implementation.ce;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class CEGatewayEventsConfig {

  public static final String CSV_CE_REQUEST_EXTRACTED = "CSV_CE_REQUEST_EXTRACTED";
  public static final String CANONICAL_CE_CREATE_SENT = "CANONICAL_CE_CREATE_SENT";

  @Bean
  public GatewayEventManager addCEEvents(GatewayEventManager gatewayEventManager) {
//    gatewayEventManager.addEventTypes(new String[] {CSV_CE_REQUEST_EXTRACTED, CANONICAL_CE_CREATE_SENT});
    return gatewayEventManager;
  }
}
