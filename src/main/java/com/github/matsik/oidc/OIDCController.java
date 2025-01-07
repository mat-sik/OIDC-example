package com.github.matsik.oidc;

import com.auth0.jwk.JwkException;
import com.github.matsik.oidc.google.GoogleConfigProps;
import com.github.matsik.oidc.google.GoogleOIDCEndpoints;
import com.github.matsik.oidc.google.GoogleService;
import com.github.matsik.oidc.jwt.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

@RestController
public class OIDCController {

    private static final int NUM_BITS = 130;
    private static final int RADIX = 32;
    private static final String RESPONSE_TYPE = "code";
    private static final String SCOPE = "openid email";
    private static final String GRANT_TYPE = "authorization_code";

    private static final String STATE_ATTR = "oauth2State";

    private final RestTemplate restTemplate;
    private final GoogleService googleService;
    private final JWTService jwtService;
    private final GoogleConfigProps props;

    private final SecureRandom secureRandom;

    private OIDCController(RestTemplate restTemplate, GoogleConfigProps props, GoogleService googleService, JWTService jwtService) {
        this.restTemplate = restTemplate;
        this.props = props;
        this.googleService = googleService;
        this.jwtService = jwtService;
        this.secureRandom = new SecureRandom();
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response, HttpSession session) throws IOException {

        String state = new BigInteger(NUM_BITS, secureRandom).toString(RADIX);

        session.setAttribute(STATE_ATTR, state);

        String nonce = new BigInteger(NUM_BITS, secureRandom).toString(RADIX);

        GoogleOIDCEndpoints endpoints = googleService.getEndpoints();

        String redirectUrl = String.format("%s?response_type=%s&client_id=%s&scope=%s&redirect_uri=%s&state=%s&nonce=%s",
                endpoints.authorizationEndpoint(),
                RESPONSE_TYPE,
                props.clientId(),
                SCOPE,
                props.redirectURI(),
                state,
                nonce
        );

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<TokenResponse> loginCallback(
            @RequestParam String state,
            @RequestParam String code,
            HttpSession session
    ) throws JwkException {
        String storedState = (String) session.getAttribute(STATE_ATTR);

        if (storedState == null || !storedState.equals(state)) {
            throw new RuntimeException("Invalid state parameter");
        }

        Map<String, String> tokenRequest = Map.of(
                "code", code,
                "client_id", props.clientId(),
                "client_secret", props.clientSecret(),
                "redirect_uri", props.redirectURI(),
                "grant_type", GRANT_TYPE
        );

        GoogleOIDCEndpoints endpoints = googleService.getEndpoints();

        TokenResponse tokenResponse = restTemplate.postForObject(endpoints.tokenEndpoint(), tokenRequest, TokenResponse.class);

        if (tokenResponse == null) {
            throw new RuntimeException("Failed to retrieve tokens");
        }

        String idToken = tokenResponse.idToken();
        jwtService.validateAndExtractClaims(idToken);

        return ResponseEntity.ok(tokenResponse);
    }
}
