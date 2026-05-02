package me.gabcytn.srsly.Service;

import java.util.*;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;
import me.gabcytn.srsly.Entity.SuggestedProblems;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Repository.ReviewProblemRepository;
import me.gabcytn.srsly.Repository.SuggestedProblemsRepository;
import org.springframework.stereotype.Service;

@Service
public class ProblemSuggestionService {
  private final ReviewProblemRepository reviewProblemRepository;
  private final UserService userService;
  private final SuggestedProblemsRepository suggestedProblemsRepository;

  public ProblemSuggestionService(
      ReviewProblemRepository reviewProblemRepository,
      UserService userService,
      SuggestedProblemsRepository suggestedProblemsRepository) {
    this.reviewProblemRepository = reviewProblemRepository;
    this.userService = userService;
    this.suggestedProblemsRepository = suggestedProblemsRepository;
  }

  public List<ProblemSummaryDto> getSuggestions() {
    User user = userService.getCurrentUser();

    Optional<SuggestedProblems> cachedProblems = suggestedProblemsRepository.findById(user.getId());
    if (cachedProblems.isPresent()) {
      return cachedProblems.get().getProblems();
    }

    List<ProblemSummaryDto> problemList = new ArrayList<>();
    reviewProblemRepository
        .findProblemsNotSolvedByUser(user.getId())
        .forEach(problem -> problemList.add(problem.summarize()));

    List<ProblemSummaryDto> result = getFiveRandomProblemsFromList(problemList);
    suggestedProblemsRepository.save(new SuggestedProblems(user.getId(), result));
    return result;
  }

  private <T> List<T> getFiveRandomProblemsFromList(List<T> list) {
    Collections.shuffle(list);
    return list.stream().limit(5).toList();
  }
}
