package me.gabcytn.srsly.Auth.DTO.Oauth;

public record GoogleApiAuthorizationResponse(
    String access_token,
    long expires_in,
    String refresh_token,
    String scope,
    String token_type,
    String id_token) {}
