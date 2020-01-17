package uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class LookupFileLoaderEventsConfig {

  public static final String POSTCODE_LOOKUP_LOADED = "POSTCODE_LOOKUP_LOADED";
  public static final String CREATED_REJECTION_FILE = "CREATED_REJECTION_FILE";

  @Bean
  public GatewayEventManager addLookupFileEvents(GatewayEventManager gatewayEventManager) {
    gatewayEventManager.addEventTypes(new String[] {POSTCODE_LOOKUP_LOADED, CREATED_REJECTION_FILE});
    return gatewayEventManager;
  }
}
