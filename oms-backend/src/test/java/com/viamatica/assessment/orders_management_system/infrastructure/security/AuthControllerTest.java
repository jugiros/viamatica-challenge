package com.viamatica.assessment.orders_management_system.infrastructure.security;

import com.viamatica.assessment.orders_management_system.infrastructure.controller.AuthController;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {AuthController.class, ProductController.class})
@Import({SecurityConfig.class, JwtService.class})
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private com.viamatica.assessment.orders_management_system.application.usecase.AuthenticateUserUseCase authenticateUserUseCase;

    @MockBean
    private com.viamatica.assessment.orders_management_system.application.usecase.RegisterUserUseCase registerUserUseCase;

    @MockBean
    private com.viamatica.assessment.orders_management_system.application.usecase.RefreshTokenUseCase refreshTokenUseCase;

    // Mocks for ProductController dependencies
    @MockBean
    private com.viamatica.assessment.orders_management_system.domain.port.ProductRepository productRepository;

    @MockBean
    private com.viamatica.assessment.orders_management_system.application.usecase.CreateProductUseCase createProductUseCase;

    @MockBean
    private com.viamatica.assessment.orders_management_system.application.usecase.UpdateProductUseCase updateProductUseCase;

    @Test
    void SECAU01_Login_ValidCredentials_ShouldReturnToken() throws Exception {
        UserDetails userDetails = User.builder()
                .username("admin@example.com")
                .password("password")
                .roles("ADMIN")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        when(userDetailsService.loadUserByUsername("admin@example.com")).thenReturn(userDetails);

        // Mock the use case response
        com.viamatica.assessment.orders_management_system.application.usecase.AuthenticateUserUseCase.Response useCaseResponse = 
            new com.viamatica.assessment.orders_management_system.application.usecase.AuthenticateUserUseCase.Response(
                "test-access-token",
                "test-refresh-token",
                null
            );
        when(authenticateUserUseCase.execute(any())).thenReturn(useCaseResponse);

        String requestBody = "{\"email\":\"admin@example.com\", \"password\":\"password\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void SECAU02_Login_InvalidPassword_ShouldReturnUnauthorized() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        when(authenticateUserUseCase.execute(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        String requestBody = "{\"email\":\"user@example.com\", \"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void SECAU03_Register_ValidData_ShouldReturnUser() throws Exception {
        // Mock the user domain response
        com.viamatica.assessment.orders_management_system.domain.entity.UserDomain mockUser = 
            com.viamatica.assessment.orders_management_system.domain.entity.UserDomain.builder()
                .id(1L)
                .name("Test User")
                .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
                .passwordHash("hashed-password")
                .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
                .active(true)
                .build();
        
        when(registerUserUseCase.execute(any())).thenReturn(mockUser);

        String requestBody = "{\"name\":\"Test User\", \"email\":\"test@example.com\", \"password\":\"password123\", \"role\":\"USER\"}";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void SECAU05_AccessProtectedEndpoint_NoToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    void SECAU06_AccessProtectedEndpoint_MalformedToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("Authorization", "Bearer abc123"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
