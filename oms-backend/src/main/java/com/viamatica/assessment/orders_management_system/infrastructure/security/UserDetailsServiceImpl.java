package com.viamatica.assessment.orders_management_system.infrastructure.security;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Email userEmail = Email.of(email);
        UserDomain user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is inactive");
        }

        return new CustomUserDetails(
                user.getId(),
                user.getEmail().value(),
                user.getPasswordHash(),
                user.isActive(),
                Collections.singletonList(new CustomGrantedAuthority(user.getRole().name()))
        );
    }

    public record CustomUserDetails(
            Long id,
            String email,
            String password,
            boolean active,
            Collection<CustomGrantedAuthority> authorities
    ) implements UserDetails {

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public Collection<CustomGrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return active;
        }
    }

    public record CustomGrantedAuthority(String role) implements org.springframework.security.core.GrantedAuthority {
        @Override
        public String getAuthority() {
            return "ROLE_" + role;
        }
    }
}
