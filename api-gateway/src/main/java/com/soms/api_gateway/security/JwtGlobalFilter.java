package com.soms.api_gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Global JWT validation filter for Spring Cloud Gateway (reactive).
 * Accepts permitPathPrefixes without token, validates JWT for other requests,
 * injects X-Auth-User and X-Auth-Roles headers for downstream services.
 *
 * Configurable via:
 *   jwt.secret (base64 or raw)
 *   jwt.permit-path-prefixes (comma separated)
 */
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtGlobalFilter.class);

    private final Key signingKey;
    private final List<String> permitPrefixes;

    public JwtGlobalFilter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.permit-path-prefixes:/auth,/actuator}") String permitCsv) {

        this.signingKey = buildKey(secret);
        this.permitPrefixes = Arrays.stream(permitCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        log.info("JwtGlobalFilter configured. permitPrefixes={}", permitPrefixes);
    }

    private Key buildKey(String secret) {
        // Support either base64-encoded key or raw secret.
        // If looks like base64 (contains '=' or is longer), decode; otherwise use bytes.
        try {
            // Try base64 decode: if success and length >= 32 bytes -> use it
            byte[] decoded = Decoders.BASE64.decode(secret);
            if (decoded.length >= 32) {
                return Keys.hmacShaKeyFor(decoded);
            }
        } catch (IllegalArgumentException ex) {
            // not base64 -> fall through
        }
        // raw bytes path
        byte[] b = secret.getBytes(StandardCharsets.UTF_8);
        if (b.length < 32) {
            throw new WeakKeyException("JWT secret is too short (must be >= 32 bytes / 256 bits). Use a base64 key or 32+ char secret.");
        }
        return Keys.hmacShaKeyFor(b);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        // permit certain prefixes (e.g., /auth, /actuator, /health)
        for (String p : permitPrefixes) {
            if (path.startsWith(p)) {
                return chain.filter(exchange);
            }
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        Jws<Claims> jws;
        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException ex) {
            log.debug("Invalid JWT: {}", ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Claims claims = jws.getBody();
        String sub = claims.getSubject();
        Object rolesObj = claims.get("roles");

        // propagate headers downstream; keep Authorization header as well
        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.headers(h -> {
                    // ensure header preserved
                    h.remove(HttpHeaders.AUTHORIZATION);
                    h.add(HttpHeaders.AUTHORIZATION, authHeader);
                    if (sub != null) h.add("X-Auth-User", sub);
                    if (rolesObj != null) {
                        String roles = convertRolesToString(rolesObj);
                        h.add("X-Auth-Roles", roles);
                    }
                })).build();

        return chain.filter(mutated);
    }

    private String convertRolesToString(Object rolesObj) {
        if (rolesObj instanceof String) return (String) rolesObj;
        return rolesObj.toString();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10; // run early but after basic filters
    }
}