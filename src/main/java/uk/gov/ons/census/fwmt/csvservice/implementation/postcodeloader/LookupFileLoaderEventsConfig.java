package uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class LookupFileLoaderEventsConfig {

  public static final String POSTCODE_LOOKUP_LOADED = "POSTCODE_LOOKUP_LOADED";

  @Bean
  public GatewayEventManager addLookupFileEvents(GatewayEventManager gatewayEventManager) {
    gatewayEventManager.addEventTypes(new String[] {POSTCODE_LOOKUP_LOADED});
    return gatewayEventManager;
  }
}
