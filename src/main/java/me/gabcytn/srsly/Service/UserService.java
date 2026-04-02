package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Auth.DTO.UserPrincipal;
import me.gabcytn.srsly.Auth.Repository.UserRepository;
import me.gabcytn.srsly.Entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
  private final UserRepository userRepository;

  private UserPrincipal getCurrentUserPrincipal() {
    try {
      return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    } catch (ClassCastException e) {
      log.error("Failed to cast to me.gabcytn.srsly.Auth.DTO.UserPrincipal");
      throw new RuntimeException(e.getMessage());
    }
  }

  public String getCurrentUserEmail() {
    return getCurrentUserPrincipal().getUsername();
  }

  public User getCurrentlyLoggedInUser() {
    return findByEmail(getCurrentUserEmail());
  }

  public User findByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.orElseThrow(
        () -> new UsernameNotFoundException("User { " + email + " } not found."));
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public Boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
