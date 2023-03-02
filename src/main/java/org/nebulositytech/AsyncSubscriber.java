package org.nebulositytech;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncSubscriber {

  @Autowired PubSubTemplate pubSubTemplate;

  @Value("${dxs.topic-name}")
  String topicName;

  @Value("${dxs.subscription-name}")
  String subscriptionName;

  public void subscribe() {
    Subscriber subscriber =
        pubSubTemplate.subscribe(
            subscriptionName,
            message -> {
              log.info(
                  "Message received from "
                      + subscriptionName
                      + " subscription: "
                      + message.getPubsubMessage().getData().toStringUtf8());
              message.ack();
            });
  }
}
