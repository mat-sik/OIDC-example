package com.github.matsik.oidc;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.matsik.oidc.google.GoogleOIDCEndpoints;
import com.github.matsik.oidc.google.GoogleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class OIDCApplication {

    public static void main(String[] args) {
        SpringApplication.run(OIDCApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JwkProvider jwkProvider(GoogleService googleService) throws MalformedURLException {
        GoogleOIDCEndpoints endpoints = googleService.getEndpoints();

        return new JwkProviderBuilder(URI.create(endpoints.jwksURI()).toURL())
                .cached(10, 24, TimeUnit.HOURS)
                .build();
    }

    @Bean
    public CacheManager googleEndpointsCache() {
        return new ConcurrentMapCacheManager("googleEndpointsCache");
    }

}
