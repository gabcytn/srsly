package me.gabcytn.srsly.DTO.Problem;

import java.time.LocalDate;

import me.gabcytn.srsly.DTO.ProblemStatus;

public record ReviewDetail(Long reviewProblemId, LocalDate lastAttemptAt, LocalDate nextAttemptAt, ProblemStatus status) {}
