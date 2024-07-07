package com.commerce.gateway.filter;

import com.commerce.gateway.exception.AuthenticationException;
import com.commerce.gateway.exception.ErrorCode;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URL;

@Component
@Slf4j
public class TokenAuthenticationFilter implements GlobalFilter {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AuthorizationHeaderFilter called. path : {}", exchange.getRequest().getPath().value());
        String jwt = extractJwt(exchange.getRequest());
        verifyJwt(jwt);
        String email = extractEmail(jwt);
        addHeader(exchange, email);
        log.info("AuthorizationHeaderFilter ending. path : {}, email : {}", exchange.getRequest().getPath().value(), email);
        return chain.filter(exchange);
    }

    private String extractJwt(ServerHttpRequest request) {
        String token = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        return token.replace("Bearer ", "");
    }

    private void verifyJwt(String token) {
        try {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(jwkSetUri));
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSObject.parse(token).getHeader().getAlgorithm(), keySource);
            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.process(SignedJWT.parse(token), null);
        } catch (Exception exception) {
            throw new AuthenticationException(ErrorCode.FAILED_VERIFY_TOKEN);
        }
    }

    private String extractEmail(String jwt) {
        try {
            var claims = SignedJWT.parse(jwt).getJWTClaimsSet();
            return claims.getSubject();
        } catch (Exception exception) {
            throw new AuthenticationException(ErrorCode.FAILED_EXTRACT_PAYLOAD);
        }
    }

    private void addHeader(ServerWebExchange exchange, String email) {
        exchange.getRequest().mutate().header("email", email);
    }
}