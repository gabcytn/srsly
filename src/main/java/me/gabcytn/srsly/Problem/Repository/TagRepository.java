package me.gabcytn.srsly.Problem.Repository;

import java.util.List;
import me.gabcytn.srsly.Problem.Entity.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends ListCrudRepository<Tag, Long> {
  List<Tag> findByNameIn(List<String> names);
}
