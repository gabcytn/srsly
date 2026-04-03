package me.gabcytn.srsly.DTO.Problem;

import java.time.LocalDate;

public record ReviewDetail(Integer srsId, LocalDate nextAttemptAt) {}
