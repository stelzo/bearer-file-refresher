package de.stelzo.dto;

public class KeycloakAuthenticateResponse {
    public String access_token;
    public Integer expires_in; // seconds
    public Integer refresh_expires_in; // seconds
    public String refresh_token;
    public String token_type;
}
