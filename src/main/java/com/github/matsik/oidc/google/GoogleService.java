package com.github.matsik.oidc.google;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleService {

    private final RestTemplate restTemplate;
    private final GoogleConfigProps props;

    public GoogleService(GoogleConfigProps props, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public GoogleOIDCEndpoints getEndpoints() {
        return restTemplate.getForObject(props.discoveryDocumentURI(), GoogleOIDCEndpoints.class);
    }

}
