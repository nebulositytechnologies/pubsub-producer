package org.nebulositytech;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.nebulositytech.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ReactiveSubscriber {

  @Autowired PubSubReactiveFactory reactiveFactory;

  int maxMessages = 100;
  boolean returnImmediately = false;
  @Autowired PubSubTemplate pubSubTemplate;

  @Value("${dxs.topic-name}")
  String topicName;

  @Value("${dxs.subscription-name}")
  String subscriptionName;

  AtomicInteger count = new AtomicInteger(0);

  Scheduler boundedElastic = Schedulers.boundedElastic();

  JacksonPubSubMessageConverter jacksonPubSubMessageConverter =
      new JacksonPubSubMessageConverter(new ObjectMapper());

  private Mono<Tuple2<AcknowledgeablePubsubMessage, PartnerProcessingState>> handleMessage(
      AcknowledgeablePubsubMessage message) {

    var counter = count.getAndIncrement();

    return Mono.fromCallable(
            () -> {
              if (counter == 25) {
                // Handle Retry Based on the type of exception and return a tuple of
                // Original
                // Message along with the
                // processing state.
                Thread.sleep(5000);
                log.info("Message: {} {}", message, counter);
                return Tuples.of(message, PartnerProcessingState.RETRY);
              }

              if (counter == 100) {
                log.info("Message: {}", counter);
                // Handle all exceptions
                return Tuples.of(message, PartnerProcessingState.FAILURE);
              }

              // If processing is successful
              //              log.info("Message: {}", counter);
              Employee employee =
                  jacksonPubSubMessageConverter.fromPubSubMessage(
                      message.getPubsubMessage(), Employee.class);
              log.info("Emp: {}", employee);
              return Tuples.of(message, PartnerProcessingState.SUCCESS);
            })
        .subscribeOn(boundedElastic)
        .timeout(Duration.ofSeconds(120))
        .onErrorResume(
            throwable -> {
              log.error("Handling Timeout", throwable);
              return Mono.just(Tuples.of(message, PartnerProcessingState.RETRY));
            });
  }

  public void pull() {
    pubSubTemplate.setMessageConverter(new JacksonPubSubMessageConverter(new ObjectMapper()));
    Flux<AcknowledgeablePubsubMessage> flux = reactiveFactory.poll(subscriptionName, 1000);
    flux.limitRate(100)
        .flatMap(this::handleMessage, 25)
        //        .doOnNext(message -> log.info(message.getT1().getPubsubMessage().))
        .subscribe(
            message -> {
              if (message.getT2() == PartnerProcessingState.SUCCESS
                  || message.getT2() == PartnerProcessingState.FAILURE) {
                message.getT1().ack();
              } else {
                log.info("Nacking the message: {}", message);
                message.getT1().nack();
              }
            });
  }
}
