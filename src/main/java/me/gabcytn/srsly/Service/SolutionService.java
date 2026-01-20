package me.gabcytn.srsly.Service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Repository.SolutionRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SolutionService {
	private final SolutionRepository solutionRepository;

	public void save(@NonNull Solution solution) {
		solutionRepository.save(solution);
	}
}
