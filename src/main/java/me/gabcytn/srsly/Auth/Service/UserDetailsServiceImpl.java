package me.gabcytn.srsly.Auth.Service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.UserPrincipal;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Auth.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      User presentUser = user.get();
      return new UserPrincipal(presentUser);
    }

    throw new UsernameNotFoundException("Username/Email not found.");
  }
}
