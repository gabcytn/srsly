package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends ListCrudRepository<Tag, Long> {
	List<Tag> findByNameIn(List<String> names);
}
