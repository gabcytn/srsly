package me.gabcytn.srsly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "me.gabcytn.srsly")
public class SrslyApplication {

  public static void main(String[] args) {
    SpringApplication.run(SrslyApplication.class, args);
  }
}
