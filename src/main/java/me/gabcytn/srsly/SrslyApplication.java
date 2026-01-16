package me.gabcytn.srsly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SrslyApplication {

  public static void main(String[] args) {
    SpringApplication.run(SrslyApplication.class, args);
  }
}
