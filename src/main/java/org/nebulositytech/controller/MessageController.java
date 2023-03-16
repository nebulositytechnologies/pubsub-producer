package org.nebulositytech.controller;

import lombok.extern.slf4j.Slf4j;
import org.nebulositytech.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/messages")
@Slf4j
public class MessageController {
  private static final Scheduler elastic = Schedulers.boundedElastic();
  @Autowired
  Sender sender;

  @PostMapping("/send")
  public void sendMessages() {
    Flux.range(0, 1000)
            .flatMap(index -> Mono.fromCallable(() -> {
              return sender.send();
            }).subscribeOn(elastic), 100)
            .subscribe(index -> log.info("{}: {}", Thread.currentThread().getName(), index));
  }

  @GetMapping("/receive")
  public void getMessages() {
  }
}
