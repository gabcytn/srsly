package me.gabcytn.srsly.AI.Response;

import me.gabcytn.srsly.Model.Quality;

public record Readability(Quality namingQuality, Quality codeStructure, Boolean commentsNeeded) {}
