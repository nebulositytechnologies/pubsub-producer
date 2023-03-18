package com.nebulositytech;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Main {

  public static void main(String[] args) {
    log.info("STARTING THE APPLICATION");
    SpringApplication.run(Main.class, args);
    log.info("APPLICATION FINISHED");
  }
}
