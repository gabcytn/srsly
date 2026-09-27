package me.gabcytn.srsly.AI.DTO;

import java.util.List;

public record Correctness(Boolean isCorrect, List<String> issues) {}
