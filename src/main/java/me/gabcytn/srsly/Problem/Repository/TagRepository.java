package me.gabcytn.srsly.Problem.Repository;

import me.gabcytn.srsly.Problem.Entity.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends ListCrudRepository<Tag, Long> {
	List<Tag> findByNameIn(List<String> names);
}
