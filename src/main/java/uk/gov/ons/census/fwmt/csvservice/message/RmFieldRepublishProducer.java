package uk.gov.ons.census.fwmt.csvservice.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtCancelActionInstruction;

@Service
public class RmFieldRepublishProducer {

  @Autowired
  @Qualifier("feedbackRabbitTemplate")
  private RabbitTemplate rabbitTemplate;

  public void republish(FwmtCancelActionInstruction fieldworkFollowup) {
    rabbitTemplate.convertAndSend("RM.Field", fieldworkFollowup);
  }

  public void republish(FwmtActionInstruction fieldworkFollowup) {
    rabbitTemplate.convertAndSend("RM.Field", fieldworkFollowup);
  }
}
