package me.gabcytn.srsly.Service;

import java.util.*;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.springframework.stereotype.Service;

@Service
public class ProblemSuggestionService {
  private final SrsProblemRepository srsProblemRepository;
  private final UserService userService;

  public ProblemSuggestionService(
      SrsProblemRepository srsProblemRepository, UserService userService) {
    this.srsProblemRepository = srsProblemRepository;
    this.userService = userService;
  }

  public List<ProblemDto> getSuggestions() {
    User user = userService.getCurrentlyLoggedInUser();
    List<Problem> srsProblems = srsProblemRepository.findProblemIdsNotSolvedByUser(user.getId());

    List<ProblemDto> problemList = new ArrayList<>();
    srsProblems.forEach(problem -> problemList.add(problem.toApiPied()));

    if (problemList.size() <= 5) {
      return problemList;
    }
    return getFiveRandomProblemsFromList(problemList);
  }

  private <T> List<T> getFiveRandomProblemsFromList(List<T> list) {
    Collections.shuffle(list);
    return list.stream().limit(5).toList();
  }
}
