package org.nebulositytech;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
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

  private Mono<String> businessProcess(AcknowledgeablePubsubMessage message) {
    message.ack();
    return Mono.just(
        new String(message.getPubsubMessage().getData().toByteArray(), Charset.defaultCharset()));
  }

  public void pull() {
    Flux<AcknowledgeablePubsubMessage> flux = reactiveFactory.poll(subscriptionName, 1000);
    flux.limitRate(100)
        .flatMap(this::businessProcess, 25)
        .doOnNext(message -> log.info("Received message number: " + message))
        .subscribe();
  }
}
