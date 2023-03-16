package org.nebulositytech;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.extern.slf4j.Slf4j;
import org.nebulositytech.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class Sender {
  @Autowired
  PubSubTemplate pubSubTemplate;

  @Value("${dxs.topic-name}")
  String topicName;

  public String send() {

    Employee employee = new Employee();
    employee.setFName("Vijay" + Math.random());
    employee.setLName("Akkineni" + Math.random());

    Map<String, String> headers = Collections.singletonMap("key", "val");
    log.info("{}: {}", Thread.currentThread().getName(), employee);
    CompletableFuture<String> completableFuture = pubSubTemplate.publish(topicName, employee, headers);

    try {
      return completableFuture.get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("exception sending!", e);
    }
    return null;
  }
}
