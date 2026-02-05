package me.gabcytn.srsly.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.TagDto;
import me.gabcytn.srsly.Entity.Tag;
import me.gabcytn.srsly.Repository.TagRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {
  private final TagRepository tagRepository;

  public List<Tag> saveAll(List<TagDto> tags) {
    List<String> tagNames = tags.stream().map(TagDto::getName).toList();
    List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
    Set<String> existingTagNames =
        existingTags.stream().map(Tag::getName).collect(Collectors.toSet());
    List<Tag> toSave =
        tags.stream()
            .filter(tag -> !existingTagNames.contains(tag.getName()))
            .map(TagDto::toEntity)
            .toList();
    if (!toSave.isEmpty()) {
      tagRepository.saveAll(toSave);
    }

    return tagRepository.findByNameIn(tagNames);
  }
}
