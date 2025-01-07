package com.github.matsik.oidc.jwt;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.matsik.oidc.google.GoogleConfigProps;
import com.github.matsik.oidc.google.GoogleOIDCEndpoints;
import com.github.matsik.oidc.google.GoogleService;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;

@Component
public class JWTService {

    private final GoogleConfigProps props;
    private final GoogleService googleService;
    private final JwkProvider provider;

    public JWTService(GoogleConfigProps props, GoogleService googleService, JwkProvider provider) {
        this.props = props;
        this.googleService = googleService;
        this.provider = provider;
    }

    public DecodedJWT validateAndExtractClaims(String idToken) throws JwkException {
        RSAPublicKey publicKey = fetchGooglePublicKey(idToken);

        GoogleOIDCEndpoints endpoints = googleService.getEndpoints();

        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(endpoints.issuer())
                .withAudience(props.clientId())
                .build();

        return verifier.verify(idToken);
    }

    private RSAPublicKey fetchGooglePublicKey(String idToken) throws JwkException {
        DecodedJWT jwt = JWT.decode(idToken);
        String kid = jwt.getKeyId();

        Jwk jwk = provider.get(kid);
        return (RSAPublicKey) jwk.getPublicKey();
    }

    public static String getEmail(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("email").asString();
    }
}
