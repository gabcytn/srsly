package me.gabcytn.srsly.DTO.Review;

import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.User;

public record InitialProblemReview(InitialReviewRequest initialReview, Problem problem, User user) {}
