package org.nebulositytech;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Play {
  public static void main(String[] args) {
    List<Integer> elements = new ArrayList<>();

    Flux.just(1, 2, 3, 4).log().subscribe(elements::add);

    log.info("elements = {} ", elements);
  }
}
