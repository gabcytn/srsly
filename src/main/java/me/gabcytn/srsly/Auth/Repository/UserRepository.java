package me.gabcytn.srsly.Auth.Repository;

import java.util.Optional;
import java.util.UUID;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);
}
