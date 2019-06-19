package uk.gov.ons.census.fwmt.csvserivce.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvserivce.config.GatewayActionsQueueConfig;

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

  @Retryable
  public void sendMessage(Object dto) throws GatewayException {
    String JSONJobRequest = convertToJSON(dto);
    rabbitTemplate
        .convertAndSend(gatewayActionsExchange.getName(), GatewayActionsQueueConfig.GATEWAY_ACTIONS_ROUTING_KEY,
            JSONJobRequest);
    log.info("Message send to queue");
  }

  protected String convertToJSON(Object dto) throws GatewayException {
    String JSONJobRequest;
    try {
      JSONJobRequest = objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, "Failed to process JSON.", e);
    }
    return JSONJobRequest;
  }
}
