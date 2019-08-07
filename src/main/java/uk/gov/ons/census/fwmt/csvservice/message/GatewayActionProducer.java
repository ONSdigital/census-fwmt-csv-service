package uk.gov.ons.census.fwmt.csvservice.message;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayActionsQueueConfig;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Slf4j
@Component
public class GatewayActionProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  @Qualifier("gatewayActionsExchange")
  private DirectExchange gatewayActionsExchange;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private GatewayEventManager gatewayEventManager;
  
  @Retryable
  public void sendMessage(CreateFieldWorkerJobRequest dto) throws GatewayException {
    String JSONJobRequest = convertToJSON(dto);
    rabbitTemplate.convertAndSend(gatewayActionsExchange.getName(), GatewayActionsQueueConfig.GATEWAY_ACTIONS_ROUTING_KEY, JSONJobRequest);
  }

  private String convertToJSON(CreateFieldWorkerJobRequest dto) throws GatewayException {
    String JSONJobRequest;
    try {
      JSONJobRequest = objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      String msg = "Failed to process JSON.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), e, msg, "<UNKNOWN>", GatewayEventsConfig.FAILED_TO_MARSHALL_CANONICAL);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, msg, e);
    }
    return JSONJobRequest;
  }
}
