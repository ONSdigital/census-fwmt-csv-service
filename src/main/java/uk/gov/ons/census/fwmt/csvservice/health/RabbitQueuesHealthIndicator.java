package uk.gov.ons.census.fwmt.csvservice.health;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class RabbitQueuesHealthIndicator extends AbstractHealthIndicator {


  private static List<String> QUEUES = Collections.emptyList();

//  private static List<String> QUEUES = Collections.singletonList(
//      GatewayActionsQueueConfig.GATEWAY_ACTIONS_QUEUE
//  );

  @Autowired
  @Qualifier("rmConnectionFactory")
  private ConnectionFactory connectionFactory;
  private RabbitAdmin rabbitAdmin;

  private boolean checkQueue(String queueName) {
    Properties properties = rabbitAdmin.getQueueProperties(queueName);
    return (properties != null);
  }

  private Map<String, Boolean> getAccessibleQueues() {
    rabbitAdmin = new RabbitAdmin(connectionFactory);

    return QUEUES.stream()
        .collect(Collectors.toMap(queueName -> queueName, this::checkQueue));
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) {
    Map<String, Boolean> accessibleQueues = getAccessibleQueues();

    builder.withDetail("accessible-queues", accessibleQueues);

    if (accessibleQueues.containsValue(false)) {
      builder.down();
    } else {
      builder.up();
    }
  }
}
