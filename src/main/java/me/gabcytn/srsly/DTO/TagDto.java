package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Tag;

public record TagDto(@JsonView(Views.Summary.class) String name) {
  public Tag toEntity() {
    return new Tag(name);
  }
}
