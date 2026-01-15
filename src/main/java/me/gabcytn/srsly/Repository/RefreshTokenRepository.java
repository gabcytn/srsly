package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.DTO.RefreshTokenValidatorDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenValidatorDto, String> {}
