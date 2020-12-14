package uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Configuration
public class AddressCheckGatewayEventsConfig {

  public static final String CSV_ADDRESS_CHECK_REQUEST_EXTRACTED = "CSV_ADDRESS_CHECK_REQUEST_EXTRACTED";
  public static final String CANONICAL_ADDRESS_CHECK_CREATE_SENT = "CANONICAL_ADDRESS_CHECK_CREATE_SENT";

  @Bean
  public GatewayEventManager addAddressCheckEvents(GatewayEventManager gatewayEventManager) {
//    gatewayEventManager
//        .addEventTypes(new String[] {CSV_ADDRESS_CHECK_REQUEST_EXTRACTED, CANONICAL_ADDRESS_CHECK_CREATE_SENT});
    return gatewayEventManager;
  }
}