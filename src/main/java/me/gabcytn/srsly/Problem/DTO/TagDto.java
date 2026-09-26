package me.gabcytn.srsly.Problem.DTO;

import me.gabcytn.srsly.Problem.Entity.Tag;

public record TagDto(String name) {
  public Tag toEntity() {
    return new Tag(name);
  }
}
