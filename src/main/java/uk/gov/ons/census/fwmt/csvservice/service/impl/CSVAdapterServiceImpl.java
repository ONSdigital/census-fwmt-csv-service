package uk.gov.ons.census.fwmt.csvservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.message.GatewayActionProducer;
import uk.gov.ons.census.fwmt.csvservice.service.CSVAdapterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Slf4j
@Service
public class CSVAdapterServiceImpl implements CSVAdapterService {

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private GatewayActionProducer jobServiceProducer;

  @Override
  public void sendJobRequest(CreateFieldWorkerJobRequest createdMessage, String event) throws GatewayException {
    jobServiceProducer.sendMessage(createdMessage);
    gatewayEventManager.triggerEvent(createdMessage.getCaseId().toString(), event);
  }
}