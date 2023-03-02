package org.nebulositytech;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

@Slf4j
public class MonoTest {
  public static void main(String[] args) {
    var inputFlux = Flux.just(1, 2, 3, 4, 5, 6, 7);
    Scheduler boundedElastic = Schedulers.boundedElastic();

    inputFlux
        .flatMap(
            integer ->
                Mono.fromCallable(
                        () -> {
                          if (integer == 3) {
                            // Handle Retry Based on the type of exception and return a tuple of
                            // Original
                            // Message along with the
                            // processing state.
                            Thread.sleep(5000);
                            log.info("Message: {}", integer);
                            return Tuples.of(integer, PartnerProcessingState.RETRY);
                          }

                          if (integer == 7) {
                            return Tuples.of(integer, PartnerProcessingState.FAILURE);
                          }

                          // If processing is successfule
                          log.info("Message: {}", integer);
                          return Tuples.of(integer, PartnerProcessingState.SUCCESS);
                        })
                    .subscribeOn(boundedElastic))
        .doOnNext(objects -> log.info("{} {}", objects.getT1(), objects.getT2()))
        .subscribe();

  }
}
