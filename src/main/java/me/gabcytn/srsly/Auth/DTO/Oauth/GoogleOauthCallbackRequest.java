package me.gabcytn.srsly.Auth.DTO.Oauth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleOauthCallbackRequest(
    @NotNull(message = "Code is required.") @NotBlank(message = "Code must not be blank.")
        String code) {}
