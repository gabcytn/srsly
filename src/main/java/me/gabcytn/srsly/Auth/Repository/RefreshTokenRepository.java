package me.gabcytn.srsly.Auth.Repository;

import me.gabcytn.srsly.Auth.DTO.RefreshTokenValidatorDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenValidatorDto, String> {}
