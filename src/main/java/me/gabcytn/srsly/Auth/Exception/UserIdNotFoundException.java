package me.gabcytn.srsly.Auth.Exception;

public class UserIdNotFoundException extends RuntimeException {
  public UserIdNotFoundException(String message) {
    super(message);
  }
}
