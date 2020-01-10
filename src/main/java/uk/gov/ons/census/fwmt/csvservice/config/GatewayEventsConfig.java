package uk.gov.ons.census.fwmt.csvservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.csvservice.Application;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class GatewayEventsConfig {

  public static final String UNABLE_TO_READ_CSV = "UNABLE_TO_READ_CSV";
  public static final String FAILED_TO_MARSHALL_CANONICAL = "FAILED_TO_MARSHALL_CANONICAL";
  public static final String FAILED_MATCH_POSTCODE = "FAILED_MATCH_POSTCODE";
  public static final String POSTCODE_MAP_EMPTY = "POSTCODE_MAP_EMPTY";
  public static final String POSTCODE_LOOKUP_LOADED = "POSTCODE_LOOKUP_LOADED";

  @Bean
  public GatewayEventManager gatewayEventManager() {
    GatewayEventManager gatewayEventManager = new GatewayEventManager();
    gatewayEventManager.setSource(Application.APPLICATION_NAME);
    gatewayEventManager.addErrorEventTypes(new String[] {UNABLE_TO_READ_CSV, FAILED_TO_MARSHALL_CANONICAL,
        FAILED_MATCH_POSTCODE, POSTCODE_MAP_EMPTY, POSTCODE_LOOKUP_LOADED});
    return gatewayEventManager;
  }
}
