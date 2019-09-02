package uk.gov.ons.fwmt.csvservice.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayActionsQueueConfig;
import uk.gov.ons.census.fwmt.csvservice.message.GatewayActionProducer;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.fwmt.csvservice.helper.FieldWorkerRequestMessageBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GatewayActionProducerTest {

  private String expectedJSON = "{\"jobIdentity\":\"testJobIdentity\",\"surveyType\":\"testSurveyType\",\"preallocatedJob\":false,\"mandatoryResourceAuthNo\":\"testMandatoryResourceAuthNo\",\"dueDate\":{\"year\":2000,\"month\":\"NOVEMBER\",\"era\":\"CE\",\"dayOfYear\":316,\"dayOfWeek\":\"SATURDAY\",\"leapYear\":true,\"dayOfMonth\":11,\"monthValue\":11,\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}},\"address\":{\"line1\":\"testLine1\",\"line2\":\"testLine2\",\"line3\":\"testLine3\",\"line4\":\"testLine4\",\"townName\":\"testTownName\",\"postCode\":\"testPostCode\",\"latitude\":1000.0,\"longitude\":1000.0}}";

  @InjectMocks
  private GatewayActionProducer gatewayActionProducer;

  @Mock
  private RabbitTemplate rabbitTemplate;

  @Mock
  private DirectExchange directExchange;

  @Captor
  private ArgumentCaptor argumentCaptor;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Test
  public void sendMessage() throws JsonProcessingException, GatewayException {
    //Given
    FieldWorkerRequestMessageBuilder messageBuilder = new FieldWorkerRequestMessageBuilder();
    CreateFieldWorkerJobRequest createJobRequest = messageBuilder.buildCreateFieldWorkerJobRequest();
    when(directExchange.getName()).thenReturn("fwmtExchange");
    when(objectMapper.writeValueAsString(eq(createJobRequest))).thenReturn(expectedJSON);

    //When
    gatewayActionProducer.sendMessage(createJobRequest);

    //Then
    verify(rabbitTemplate)
        .convertAndSend(eq("fwmtExchange"), eq(GatewayActionsQueueConfig.GATEWAY_ACTIONS_ROUTING_KEY),
            argumentCaptor.capture());
    String result = String.valueOf(argumentCaptor.getValue());

    assertEquals(expectedJSON, result);
  }

  @Test(expected = GatewayException.class)
  public void sendBadMessage() throws JsonProcessingException, GatewayException {
    //Given
    FieldWorkerRequestMessageBuilder messageBuilder = new FieldWorkerRequestMessageBuilder();
    CreateFieldWorkerJobRequest createJobRequest = messageBuilder.buildCreateFieldWorkerJobRequest();
    when(objectMapper.writeValueAsString(eq(createJobRequest))).thenThrow(new JsonProcessingException("Error") {
    });

    //When
    gatewayActionProducer.sendMessage(createJobRequest);
  }
}