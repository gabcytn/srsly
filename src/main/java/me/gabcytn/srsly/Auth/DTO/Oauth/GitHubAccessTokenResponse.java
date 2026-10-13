package me.gabcytn.srsly.Auth.DTO.Oauth;

public record GitHubAccessTokenResponse(
    String access_token,
    long expires_in,
    String refresh_token,
    long refresh_token_expires_in,
    String token_type,
    String scope) {}
