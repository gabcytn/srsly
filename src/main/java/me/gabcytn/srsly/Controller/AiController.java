package me.gabcytn.srsly.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.AI.AiCritique;
import me.gabcytn.srsly.Service.AiService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/solutions")
public class AiController {
  private final AiService aiService;

  @Tag(name = "AI Feedback")
  @Operation(summary = "Generate AI Critique")
  @ApiResponse(responseCode = "200")
  @PostMapping("/{solutionId}/ai")
  public AiCritique index(@PathVariable long solutionId) {
    return aiService.critique(solutionId);
  }
}
