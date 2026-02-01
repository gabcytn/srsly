package me.gabcytn.srsly.AI;

import me.gabcytn.srsly.AI.Response.*;

public record AiResponse(
    Summary summary,
    Correctness correctness,
    Complexity complexity,
    Readability readability,
    Bugs bugs,
    Improvements improvements) {}
