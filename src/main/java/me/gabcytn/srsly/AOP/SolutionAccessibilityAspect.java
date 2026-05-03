package me.gabcytn.srsly.AOP;

import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.GenericForbiddenException;
import me.gabcytn.srsly.Service.SolutionService;
import me.gabcytn.srsly.Service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SolutionAccessibilityAspect {
  private final UserService userService;
  private final SolutionService solutionService;

  public SolutionAccessibilityAspect(SolutionService solutionService, UserService userService) {
    this.solutionService = solutionService;
    this.userService = userService;
  }

  @Around("@annotation(me.gabcytn.srsly.AOP.Annotation.VerifySolutionOwner)")
  public Object validateAccessibility(ProceedingJoinPoint pjp) throws Throwable {
    long id = (long) pjp.getArgs()[0];
    Solution solution = solutionService.findById(id);

    if (isCurrentUserAllowedAccessToSolution(solution)) {
      return pjp.proceed();
    }

    log.warn("User is trying to access foreign resource.");
    throw new GenericForbiddenException("Invalid resource access.");
  }

  private boolean isCurrentUserAllowedAccessToSolution(Solution solution) {
    User user = userService.getCurrentUser();
    return solution.getSolvedProblem().getUser().getId().equals(user.getId());
  }
}
