package me.gabcytn.srsly.Auth.DTO;

public record AuthResponse(String email, Boolean isVerified, JwtResponse jwtResponse) {}
