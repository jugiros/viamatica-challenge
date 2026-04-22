package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidPasswordException;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Use case for user authentication.
 * Validates credentials, generates JWT + RefreshToken, and logs audit event.
 */
@Service
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final AuditPort auditPort;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Key jwtKey;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public AuthenticateUserUseCase(
            UserRepository userRepository,
            AuditPort auditPort,
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.expiration}") long jwtExpiration,
            @Value("${security.jwt.refresh-expiration}") long refreshExpiration) {
        this.userRepository = userRepository;
        this.auditPort = auditPort;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public record Command(
            String email,
            String password
    ) {}

    public record Response(
            String accessToken,
            String refreshToken,
            UserDomain user
    ) {}

    public Response execute(Command command) {
        Email email = Email.of(command.email);
        UserDomain user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(command.password, user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password");
        }

        if (!user.isActive()) {
            throw new InvalidPasswordException("User account is inactive");
        }

        String accessToken = generateToken(user.getId(), jwtExpiration);
        String refreshToken = generateToken(user.getId(), refreshExpiration);

        auditPort.logLogin(user.getId(), "0.0.0.0", "API");

        return new Response(accessToken, refreshToken, user);
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

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
