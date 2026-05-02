package me.gabcytn.srsly.DTO;

import me.gabcytn.srsly.Exception.GenericNotFoundException;

public enum Difficulty {
  Easy,
  Medium,
  Hard;

  public static Difficulty fromStringOrElseThrow(String difficulty) {
    try {
      return Enum.valueOf(Difficulty.class, difficulty);
    } catch (IllegalArgumentException e) {
      throw new GenericNotFoundException("Difficulty not found.");
    }
  }
}
