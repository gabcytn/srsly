package me.gabcytn.srsly.AI.Response;

import java.util.List;

public record Correctness(Boolean isCorrect, List<String> issues) {}
