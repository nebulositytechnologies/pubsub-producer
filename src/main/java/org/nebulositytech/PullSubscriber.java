package org.nebulositytech;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PullSubscriber {

  int maxMessages = 100;
  boolean returnImmediately = false;
  @Autowired PubSubTemplate pubSubTemplate;

  @Value("${dxs.topic-name}")
  String topicName;

  @Value("${dxs.subscription-name}")
  String subscriptionName;

  public void pull() {
    List<AcknowledgeablePubsubMessage> messages =
        pubSubTemplate.pull(subscriptionName, maxMessages, returnImmediately);

    messages.forEach(message -> log.info(message.getPubsubMessage().getData().toStringUtf8()));

    // acknowledge the messages
    pubSubTemplate.ack(messages);
  }
}
