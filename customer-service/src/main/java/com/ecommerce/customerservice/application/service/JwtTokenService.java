package com.ecommerce.customerservice.application.service;

import com.ecommerce.customerservice.domain.model.Customer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {

    private static final String DEFAULT_SECRET = "customer-service-dev-secret-change-me";

    private final String secret;
    private final Duration expiration;

    public JwtTokenService() {
        this(DEFAULT_SECRET, 60L);
    }

    @Autowired
    public JwtTokenService(
            @Value("${app.jwt.secret:customer-service-dev-secret-change-me}") String secret,
            @Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.secret = secret;
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public String generateToken(Customer customer) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(customer.getEmail())
                .claim("customerId", customer.getId())
                .claim("fullName", customer.getFullName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }
}
