package me.gabcytn.srsly.Proxy;

import me.gabcytn.srsly.DTO.LeetCodeApiPied;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${openfeign.client.url}", name = "placeholder")
public interface LeetCodeQuestionProxy {
  @GetMapping("/problem/{id}")
  LeetCodeApiPied getProblem(@PathVariable int id);
}
