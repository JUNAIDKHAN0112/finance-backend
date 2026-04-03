package com.zorvyn.finance.service;

import com.zorvyn.finance.common.exception.BadRequestException;
import com.zorvyn.finance.dto.request.LoginRequest;
import com.zorvyn.finance.dto.request.RegisterRequest;
import com.zorvyn.finance.dto.response.AuthResponse;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.Role;
import com.zorvyn.finance.repository.UserRepository;
import com.zorvyn.finance.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@test.com")
                .password("encodedPassword")
                .role(Role.VIEWER)
                .isActive(true)
                .build());
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test@test.com", response.getEmail());
        assertEquals("VIEWER", response.getRole());
        assertEquals("mockToken", response.getToken());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void register_DuplicateEmail_ThrowsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test");
        request.setEmail("existing@test.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@test.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .isActive(true)
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("ADMIN", response.getRole());
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void login_WrongPassword_ThrowsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongpass");

        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .isActive(true)
                .role(Role.VIEWER)
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));
    }

    @Test
    void login_DeactivatedUser_ThrowsBadRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        User user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .role(Role.VIEWER)
                .isActive(false)
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class,
                () -> authService.login(request));
    }

    @Test
    void login_UserNotFound_ThrowsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(request));
    }
}