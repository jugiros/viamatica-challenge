package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final UserRepository userRepository;
    private final SecretKey jwtKey;
    private final long jwtExpiration;

    public RefreshTokenUseCase(
            UserRepository userRepository,
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.expiration}") long jwtExpiration) {
        this.userRepository = userRepository;
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
    }

    public record Command(String refreshToken) {}

    public record Response(String accessToken) {}

    public Response execute(Command command) {
        Claims claims = validateToken(command.refreshToken);
        Long userId = Long.parseLong(claims.getSubject());

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String accessToken = generateToken(userId, jwtExpiration);

        return new Response(accessToken);
    }

    private String generateToken(Long userId, long expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)))
                .id(UUID.randomUUID().toString())
                .signWith(jwtKey)
                .compact();
    }

    private Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
