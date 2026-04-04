package me.gabcytn.srsly.DTO;

import me.gabcytn.srsly.Entity.Tag;

public record TagDto(String name) {
  public Tag toEntity() {
    return new Tag(name);
  }
}
