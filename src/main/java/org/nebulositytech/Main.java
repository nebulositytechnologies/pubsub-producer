package org.nebulositytech;

import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Main implements CommandLineRunner {

  @Autowired Sender sender;
  @Autowired AsyncSubscriber asyncSubscriber;
  @Autowired KeepSubscriptionAlive keepSubscriptionAlive;
  @Autowired ReactiveSubscriber reactiveSubscriber;
  @Autowired PullSubscriber pullSubscriber;

  public static void main(String[] args) {
    log.info("STARTING THE APPLICATION");
    SpringApplication.run(Main.class, args);
    log.info("APPLICATION FINISHED");
  }

  @Override
  public void run(String... args) throws ExecutionException, InterruptedException {
    //    log.info("EXECUTING : command line runner");
    //    for (int i = 0; i < 1000; ++i) {
    //      sender.send();
    //    }

    reactiveSubscriber.pull();
  }
}
