package uk.gov.ons.census.fwmt.csvservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.message.GatewayActionProducer;
import uk.gov.ons.census.fwmt.csvservice.service.CSVAdapterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.time.LocalTime;

import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CANONICAL_CREATE_SENT;

@Slf4j
@Component
public class CSVAdapterServiceImpl implements CSVAdapterService {


  @Autowired
  private GatewayEventManager gatewayEventManager = new GatewayEventManager();

  @Autowired
  private GatewayActionProducer jobServiceProducer = new GatewayActionProducer();

  @Override
  public void sendJobRequest(CreateFieldWorkerJobRequest ceCreateCase) throws GatewayException {
    jobServiceProducer.sendMessage(ceCreateCase);
    gatewayEventManager
        .triggerEvent(ceCreateCase.getCaseId().toString(), CANONICAL_CREATE_SENT, LocalTime.now());
  }
}