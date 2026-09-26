package me.gabcytn.srsly.Review.DTO;

import me.gabcytn.srsly.Problem.Entity.SolvedProblem;

public record InitialProblemReview(
    InitialReviewRequest initialReview, SolvedProblem solvedProblem) {}
