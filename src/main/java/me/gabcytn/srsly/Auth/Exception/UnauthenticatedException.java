package me.gabcytn.srsly.Auth.Exception;

public class UnauthenticatedException extends RuntimeException {
  public UnauthenticatedException(String message) {
    super(message);
  }
}
