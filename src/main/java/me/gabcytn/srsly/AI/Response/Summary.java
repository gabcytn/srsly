package me.gabcytn.srsly.AI.Response;

import me.gabcytn.srsly.Model.Confidence;
import me.gabcytn.srsly.Model.Rating;
import me.gabcytn.srsly.Model.Verdict;

public record Summary(Rating overallRating, Verdict verdict, Confidence confidence) {}
