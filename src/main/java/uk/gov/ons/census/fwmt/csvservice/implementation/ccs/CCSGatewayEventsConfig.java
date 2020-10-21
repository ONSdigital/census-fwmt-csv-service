package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class CCSGatewayEventsConfig {

  @Bean
  public GatewayEventManager addCCSEvents(GatewayEventManager gatewayEventManager) {
    return gatewayEventManager;
  }
}
