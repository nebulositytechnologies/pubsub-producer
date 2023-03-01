package org.nebulositytech;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Main implements CommandLineRunner {
  public static void main(String[] args) {
    log.info("STARTING THE APPLICATION");
    SpringApplication.run(Main.class, args);
    log.info("APPLICATION FINISHED");
  }

  @Override
  public void run(String... args) {
    log.info("EXECUTING : command line runner");
    for (int i = 0; i < args.length; ++i) {
      log.info("args[{}]: {}", i, args[i]);
    }
  }
}
