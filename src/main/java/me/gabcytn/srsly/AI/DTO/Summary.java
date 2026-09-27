package me.gabcytn.srsly.AI.DTO;

import me.gabcytn.srsly.Problem.DTO.Confidence;

public record Summary(Rating overallRating, Verdict verdict, Confidence confidence) {}
