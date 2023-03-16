package org.nebulositytech.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.nebulositytech.MessageHandler;
import org.nebulositytech.model.Employee;
import org.nebulositytech.model.PartnerProcessingState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

@Configuration
@Slf4j
public class PubSubConfig {

  @Value("${dxs.subscription-name}")
  String subscriptionName;

  @Bean
  public PubSubMessageConverter jacksonPubSubMessageConverter() {
    return new JacksonPubSubMessageConverter(new ObjectMapper());
  }

  @Bean
  public PubSubTemplate pubSubTemplate(SubscriberFactory subscriberFactory,
                                        PublisherFactory publisherFactory,
                                        PubSubMessageConverter pubSubMessageConverter) {
    var pubSubTemplate = new PubSubTemplate(publisherFactory, subscriberFactory);
    pubSubTemplate.setMessageConverter(pubSubMessageConverter);
    return pubSubTemplate;
  }

  @Bean
  public PubSubReactiveFactory pubSubReactiveFactory(
          PubSubTemplate pubSubTemplate) {
    return new PubSubReactiveFactory(pubSubTemplate, Schedulers.parallel());
  }


  @Bean
  public ApplicationRunner reactiveSubscriber(PubSubReactiveFactory reactiveFactory,
                                               PubSubMessageConverter converter) {
    return args -> reactiveFactory
            .poll(subscriptionName, 1000)
//              .limitRate(500)
            .flatMap(message -> {
              Employee employee = converter.fromPubSubMessage(message.getPubsubMessage(), Employee.class);
              return MessageHandler.handleMessage(message, employee);
            }, 25).subscribe(
                    message -> {
                      if (message.getT2() == PartnerProcessingState.SUCCESS
                              || message.getT2() == PartnerProcessingState.FAILURE) {
                        log.info("acking the message: {} {}", message.getT1().getPubsubMessage().getMessageId(),
                                message.getT1().getAckId());
                        message.getT1().ack();
                      } else {
                        log.info("nacking the message: {} {}", message.getT1().getPubsubMessage().getMessageId(),
                                message.getT1().getAckId());
                        message.getT1().nack();
                      }
                    });
  }

}
