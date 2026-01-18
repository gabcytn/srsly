package me.gabcytn.srsly.Controller;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Auth.Exception.UserIdNotFoundException;
import me.gabcytn.srsly.Auth.Repository.UserRepository;
import me.gabcytn.srsly.Entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserRepository userRepository;

  @GetMapping
  public Iterable<User> getUsers() {
    return userRepository.findAll();
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable UUID id) {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new UserIdNotFoundException("User with the given ID is not found.");
    }
    return user.get();
  }
}
