package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Tag;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
  @JsonView(Views.Summary.class)
  private String name;

  public Tag toEntity() {
    return new Tag(name);
  }
}
