package com.github.matsik.oidc.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth2.client.google")
public record GoogleConfigProps(
        String clientId,
        String clientSecret,
        String discoveryDocumentURI,
        String redirectURI
) {
}
