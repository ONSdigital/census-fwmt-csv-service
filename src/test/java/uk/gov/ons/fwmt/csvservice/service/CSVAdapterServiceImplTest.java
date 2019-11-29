package uk.gov.ons.fwmt.csvservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.message.GatewayActionProducer;
import uk.gov.ons.census.fwmt.csvservice.service.ce.CSVAdapterServiceImpl;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;
import uk.gov.ons.fwmt.csvservice.helper.FieldWorkerRequestMessageBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CANONICAL_CCS_CREATE_SENT;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CANONICAL_CE_CREATE_SENT;

@RunWith(MockitoJUnitRunner.class)
public class CSVAdapterServiceImplTest {

  @InjectMocks
  private CSVAdapterServiceImpl csvAdapterServiceImpl;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Mock
  private GatewayActionProducer gatewayActionProducer;

  @Test
  public void sendCCSRequestToJobService() throws GatewayException {
    // Given
    CreateFieldWorkerJobRequest createJobRequest = new FieldWorkerRequestMessageBuilder()
        .buildCreateFieldWorkerJobRequestCCS();

    // When
    csvAdapterServiceImpl.sendJobRequest(createJobRequest, CANONICAL_CCS_CREATE_SENT);

    // Then
    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CANONICAL_CCS_CREATE_SENT));
  }

  @Test
  public void sendCERequestToJobService() throws GatewayException {
    // Given
    CreateFieldWorkerJobRequest createJobRequest = new FieldWorkerRequestMessageBuilder()
            .buildCreateFieldWorkerJobRequestCE();

    // When
    csvAdapterServiceImpl.sendJobRequest(createJobRequest,CANONICAL_CE_CREATE_SENT);

    // Then
    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CANONICAL_CE_CREATE_SENT));
  }

}
