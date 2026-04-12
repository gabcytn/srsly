package me.gabcytn.srsly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "me.gabcytn.srsly")
@EnableScheduling
@EnableAsync
public class SrslyApplication {

  public static void main(String[] args) {
    SpringApplication.run(SrslyApplication.class, args);
  }
}
