package org.nebulositytech;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KeepSubscriptionAlive {

  @Autowired PubSubTemplate pubSubTemplate;

  @Value("${dxs.topic-name}")
  String topicName;

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void fakeScheduledTask() {
    // do nothing
  }
}
