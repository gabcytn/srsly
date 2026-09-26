package me.gabcytn.srsly.Problem.Proxy;

import me.gabcytn.srsly.Problem.DTO.LeetCodeProblemApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "${openfeign.client.url}", name = "placeholder")
public interface LeetCodeQuestionProxy {
  @GetMapping("/problem/{id}")
  LeetCodeProblemApiResponse getProblem(@PathVariable int id);
}
