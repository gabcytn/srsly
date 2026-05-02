package me.gabcytn.srsly.DTO.Review;

import me.gabcytn.srsly.Entity.SolvedProblem;

public record InitialProblemReview(
    InitialReviewRequest initialReview, SolvedProblem solvedProblem) {}
