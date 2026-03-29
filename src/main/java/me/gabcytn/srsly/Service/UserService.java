package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.UserPrincipal;
import me.gabcytn.srsly.Auth.Repository.UserRepository;
import me.gabcytn.srsly.Entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;

  public User getCurrentlyLoggedInUser() {
    try {
      UserPrincipal principal =
          (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      return findByEmail(principal.getUsername());
    } catch (ClassCastException e) {
      LOGGER.error("Failed to cast to me.gabcytn.srsly.Auth.DTO.UserPrincipal");
      throw new RuntimeException(e.getMessage());
    }
  }

  public User findByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User { " + email + " } not found.");
    }
    return user.get();
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public Boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
