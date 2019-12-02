package uk.gov.ons.fwmt.csvservice.message;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.message.GatewayActionProducer;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.fwmt.csvservice.helper.FieldWorkerRequestMessageBuilder;

@RunWith(MockitoJUnitRunner.class)
public class GatewayActionProducerTest {

  @InjectMocks
  private GatewayActionProducer gatewayActionProducer;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Test(expected = GatewayException.class)
  public void sendBadMessage() throws JsonProcessingException, GatewayException {
    //Given
    FieldWorkerRequestMessageBuilder messageBuilder = new FieldWorkerRequestMessageBuilder();
    CreateFieldWorkerJobRequest createJobRequest = messageBuilder.buildCreateFieldWorkerJobRequestCCS();
    when(objectMapper.writeValueAsString(eq(createJobRequest))).thenThrow(new JsonProcessingException("Error") {
    });

    //When
    gatewayActionProducer.sendMessage(createJobRequest);
  }
}