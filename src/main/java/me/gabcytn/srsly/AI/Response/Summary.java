package me.gabcytn.srsly.AI.Response;

import me.gabcytn.srsly.DTO.Confidence;

public record Summary(Rating overallRating, Verdict verdict, Confidence confidence) {}
