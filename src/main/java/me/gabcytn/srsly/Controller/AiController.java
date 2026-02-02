package me.gabcytn.srsly.Controller;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.AI.AiCritique;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Service.SolutionService;
import org.jsoup.Jsoup;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/solutions")
public class AiController {
  private final ChatModel chatModel;
  private final SolutionService solutionService;

  @GetMapping("/{solutionId}/ai")
  public AiCritique index(@PathVariable int solutionId) {
    Solution solution = solutionService.findById(solutionId);
		String problemDescription = Jsoup.parse(solution.getProblem().getQuestion()).text();
    String model = "gemini-2.5-flash-lite";
    AiCritique response =
        ChatClient.create(chatModel)
            .prompt()
            .options(GoogleGenAiChatOptions.builder().model(model).build())
            .system(
                """
				You are a senior software engineer and algorithm reviewer.
				Your task is to analyze a given LeetCode solution and evaluate it objectively.
				Do not assume correctness. Verify it.
				Be strict, technical, and concise.
				""")
            .user(
                u ->
                    u.text(
                            """
				You are given:
				1. The LeetCode problem description
				2. The submitted solution code

				Analyze the solution across the following dimensions:
				- Correctness
				- Time and Space complexity
				- Readability and code quality
				- Potential bugs or logical flaws
				- Possible improvements or alternatives

				### Input
				  - "problem": "{problem}",
				  - "solution": "{solution}"
				""")
                        .param("problem", problemDescription)
                        .param("solution", solution.getCode()))
            .call()
            .entity(AiCritique.class);

		solution.setAiCritique(response);
		solutionService.save(solution);
    return response;
  }
}
