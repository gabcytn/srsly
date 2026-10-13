package me.gabcytn.srsly.Auth.DTO.Oauth;

public enum SignUpMethod {
  GOOGLE("google"),
  GITHUB("github");

  private String provider;

  SignUpMethod(String provider) {
    this.provider = provider;
  }

  public String getProvider() {
    return provider;
  }
}
