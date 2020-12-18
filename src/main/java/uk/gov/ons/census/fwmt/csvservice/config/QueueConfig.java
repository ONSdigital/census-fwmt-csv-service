package uk.gov.ons.census.fwmt.csvservice.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import uk.gov.ons.census.fwmt.common.retry.GatewayMessageRecover;

@Configuration
public class QueueConfig {
  private String username;
  private String password;
  private String hostname;
  private int port;
  private String virtualHost;

  public QueueConfig(
      @Value("${app.rabbitmq.rm.username}") String username,
      @Value("${app.rabbitmq.rm.password}") String password,
      @Value("${app.rabbitmq.rm.host}") String hostname,
      @Value("${app.rabbitmq.rm.port}") int port,
      @Value("${app.rabbitmq.rm.virtualHost}") String virtualHost) {
    this.username = username;
    this.password = password;
    this.hostname = hostname;
    this.port = port;
    this.virtualHost = virtualHost;
  }

  public static CachingConnectionFactory createConnectionFactory(int port, String hostname, String virtualHost,
      String password, String username) {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(hostname, port);

    cachingConnectionFactory.setVirtualHost(virtualHost);
    cachingConnectionFactory.setPassword(password);
    cachingConnectionFactory.setUsername(username);

    return cachingConnectionFactory;
  }

  // Interceptor
  @Bean
  public RetryOperationsInterceptor interceptor(
      @Qualifier("retryTemplate") RetryOperations retryOperations) {
    RetryOperationsInterceptor interceptor = new RetryOperationsInterceptor();
    interceptor.setRecoverer(new GatewayMessageRecover());
    interceptor.setRetryOperations(retryOperations);
    return interceptor;
  }

  @Bean
  public AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(connectionFactory());
  }

  // Connection Factory
  @Bean("rmConnectionFactory")
  public ConnectionFactory connectionFactory() {
    return createConnectionFactory(port, hostname, virtualHost, password, username);
  }
}
