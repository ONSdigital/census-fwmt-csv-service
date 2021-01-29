package uk.gov.ons.census.fwmt.csvservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.census.fwmt.csvservice.Application;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.census.fwmt.events.producer.GatewayLoggingEventProducer;
import uk.gov.ons.census.fwmt.events.producer.RabbitMQGatewayEventProducer;

import java.util.Arrays;

@Slf4j
@Configuration
public class GatewayEventsConfig {

  @Value("${app.testing}")
  private boolean testing;

  @Bean
  public GatewayEventManager gatewayEventManager(GatewayLoggingEventProducer gatewayLoggingEventProducer, RabbitMQGatewayEventProducer testProducer) {

    final GatewayEventManager gatewayEventManager;
    if (testing) {
      log.warn("\n\n \t IMPORTANT - Test Mode: ON        \n \t\t Service is initiated in test mode which, this should not occur in production \n\n");
      gatewayEventManager = new GatewayEventManager(Arrays.asList(gatewayLoggingEventProducer, testProducer));
    } else {
      log.warn("\n\n \t IMPORTANT - Test Mode: OFF   \n\n");
      gatewayEventManager = new GatewayEventManager(Arrays.asList(gatewayLoggingEventProducer));
    }

    gatewayEventManager.setSource(Application.APPLICATION_NAME);
    return gatewayEventManager;
  }
}
