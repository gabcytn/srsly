package me.gabcytn.srsly.Service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.TagDto;
import me.gabcytn.srsly.Entity.Tag;
import me.gabcytn.srsly.Repository.TagRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {
  private final TagRepository tagRepository;

  public List<Tag> save(List<TagDto> tags) {
    return tagRepository.saveAll(tags.stream().map(TagDto::toEntity).toList());
  }
}
