package com.github.matsik.oidc.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleOIDCEndpoints(
        @JsonProperty("issuer") String issuer,
        @JsonProperty("authorization_endpoint") String authorizationEndpoint,
        @JsonProperty("token_endpoint") String tokenEndpoint,
        @JsonProperty("jwks_uri") String jwksURI
) {
}
