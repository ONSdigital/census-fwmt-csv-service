package uk.gov.ons.census.fwmt.csvservice.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayActionsQueueConfig;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

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
    Message gatewayMessage = convertJSONToMessage(JSONJobRequest);

    rabbitTemplate.convertAndSend(gatewayActionsExchange.getName(), GatewayActionsQueueConfig.GATEWAY_ACTIONS_ROUTING_KEY, gatewayMessage);
  }

  private String convertToJSON(CreateFieldWorkerJobRequest dto) throws GatewayException {
    String JSONJobRequest;
    try {
      JSONJobRequest = objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      String msg = "Failed to process JSON.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), e, msg, String.valueOf(dto.getCaseId()),
          GatewayEventsConfig.FAILED_TO_MARSHALL_CANONICAL);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, msg, e);
    }
    return JSONJobRequest;
  }
  
  private Message convertJSONToMessage(String messageJSON) {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setContentType("application/json");
    MessageConverter messageConverter = new Jackson2JsonMessageConverter();

    return messageConverter.toMessage(messageJSON, messageProperties);
  }
}
