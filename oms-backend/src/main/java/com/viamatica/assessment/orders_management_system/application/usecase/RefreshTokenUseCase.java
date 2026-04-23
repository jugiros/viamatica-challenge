package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public record Command(String refreshToken) {}

    public record Response(String accessToken) {}

    public Response execute(Command command) {
        String userIdStr = jwtService.extractUsername(command.refreshToken);
        Long userId = Long.parseLong(userIdStr);

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String accessToken = jwtService.generateAccessToken(userId);

        return new Response(accessToken);
    }
}
