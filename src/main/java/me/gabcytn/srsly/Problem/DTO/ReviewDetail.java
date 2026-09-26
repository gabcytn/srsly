package me.gabcytn.srsly.Problem.DTO;

import java.time.LocalDate;

public record ReviewDetail(Long reviewProblemId, LocalDate lastAttemptAt, LocalDate nextAttemptAt, ProblemStatus status) {}
