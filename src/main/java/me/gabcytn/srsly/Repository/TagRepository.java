package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends ListCrudRepository<Tag, Long> {}
