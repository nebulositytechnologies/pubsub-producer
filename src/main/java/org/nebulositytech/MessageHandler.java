package org.nebulositytech;

import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nebulositytech.model.Employee;
import org.nebulositytech.model.PartnerProcessingState;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageHandler {
  static AtomicInteger count = new AtomicInteger(0);
  static Scheduler boundedElastic = Schedulers.boundedElastic();

  public static Mono<Tuple2<AcknowledgeablePubsubMessage, PartnerProcessingState>> handleMessage(
          AcknowledgeablePubsubMessage message, Employee employee) {
    var counter = count.getAndIncrement();
    return Mono.fromCallable(
                    () -> {
                      if (counter == 25) {
                        Thread.sleep(5000);
                        log.info("Message: {} {}", message, counter);
                        return Tuples.of(message, PartnerProcessingState.RETRY);
                      }
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
}
