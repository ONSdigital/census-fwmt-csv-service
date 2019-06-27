package uk.gov.ons.census.fwmt.csvservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class GatewayEventsConfig {

  public static final String CANONICAL_CREATE_SENT = "Canonical - Action Create Sent";
  public static final String CSV_REQUEST_EXTRACTED = "CSV Service - Request extracted";

  @Bean
  public GatewayEventManager gatewayEventManager() {
    GatewayEventManager gatewayEventManager = new GatewayEventManager();
    gatewayEventManager.addEventTypes(new String[] {CANONICAL_CREATE_SENT, CSV_REQUEST_EXTRACTED});
    return gatewayEventManager;
  }
}
