package uk.gov.ons.census.fwmt.csvservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class GatewayEventsConfig {

  public static final String CANONICAL_CREATE_SENT = "Canonical - Action Create Sent";
  public static final String CSV_CCS_REQUEST_EXTRACTED = "CSV Service - CCS Request extracted";
  public static final String CSV_CE_REQUEST_EXTRACTED = "CSV Service - CE Request extracted";

  @Bean
  public GatewayEventManager gatewayEventManager() {
    GatewayEventManager gatewayEventManager = new GatewayEventManager();
    gatewayEventManager.addEventTypes(new String[] {CANONICAL_CREATE_SENT, CSV_CCS_REQUEST_EXTRACTED, CSV_CE_REQUEST_EXTRACTED});
    return gatewayEventManager;
  }
}
