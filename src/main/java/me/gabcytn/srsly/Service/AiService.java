package me.gabcytn.srsly.Service;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.AI.AiCritique;
import me.gabcytn.srsly.Entity.Solution;
import org.jsoup.Jsoup;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AiService {
	private final ChatModel chatModel;
	private final SolutionService solutionService;

	public AiCritique critique(int solutionId) {
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
