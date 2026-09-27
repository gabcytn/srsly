package me.gabcytn.srsly.AI.DTO;

public record AiCritique(
    Summary summary,
    Correctness correctness,
    Complexity complexity,
    Readability readability,
    Bugs bugs,
    Improvements improvements) {}
