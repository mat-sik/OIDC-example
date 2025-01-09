# OIDC-example

This project demonstrates the implementation of an OpenID Connect (OIDC) authentication system without relying on any
external libraries. The goal is to provide a deeper understanding of each step in the OIDC authentication flow.

Google is used as the authentication provider, following their
official [OpenID Connect guide](https://developers.google.com/identity/openid-connect/openid-connect).

## Spring Security

Additionally, there is a second branch in this repository that leverages a dedicated OIDC library from Spring. Spring
requires that the authentication is stateful(you can't pass JWT id token with every request). Because of this, I use
redis as session store to allow for potential horizontal scaling, I've done it also by using dedicated Spring library.

You can explore the second branch here: [spring-security](https://github.com/mat-sik/OIDC-example/tree/spring-security).
