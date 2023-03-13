package org.nebulositytech;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
@Slf4j
public class Main implements CommandLineRunner {

  @Autowired Sender sender;
  @Autowired ReactiveSubscriber reactiveSubscriber;

  public static void main(String[] args) {
    log.info("STARTING THE APPLICATION");
    SpringApplication.run(Main.class, args);
    log.info("APPLICATION FINISHED");
  }

  private static final Scheduler elastic = Schedulers.boundedElastic();

  @Override
  public void run(String... args) throws ExecutionException, InterruptedException {
    log.info("EXECUTING : command line runner");
    log.info("Program thread :: " + Thread.currentThread().getName());

    //    Flux.range(0, 1000)
    //        .flatMap(
    //            index ->
    //                Mono.fromCallable(
    //                        () -> {
    //                          sender.send();
    //                          return Mono.just(index);
    //                        })
    //                    .subscribeOn(elastic),
    //            100)
    //        .log()
    //        //        .subscribeOn(elastic)
    //        .subscribe(index -> log.info("{}: {}", Thread.currentThread().getName(), index));

    reactiveSubscriber.pull();
  }
}
