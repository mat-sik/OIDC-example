package com.github.matsik.oidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class OIDCApplication {

    public static void main(String[] args) {
        SpringApplication.run(OIDCApplication.class, args);
    }

}
