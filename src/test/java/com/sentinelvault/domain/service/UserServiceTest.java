package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.mapper.UserMapper;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.UserEntity;
import com.sentinelvault.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User domainUser;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        domainUser = new User();
        domainUser.setFullName("Lucas Draco");
        domainUser.setEmail("lucas.teste@sentinelvault.com");
        domainUser.setPassword("SafePassword123");

        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setFullName("Lucas Draco");
        userEntity.setEmail("lucas.teste@sentinelvault.com");
        userEntity.setPasswordHash("encodedHashedPassword");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.findByEmail(domainUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(domainUser.getPassword())).thenReturn("encodedHashedPassword");
        when(userMapper.toEntity(domainUser)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(domainUser);

        User registered = userService.registerUser(domainUser);

        assertNotNull(registered);
        assertEquals(domainUser.getEmail(), registered.getEmail());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void shouldThrowExceptionWhenRegisteringWithExistingEmail() {
        when(userRepository.findByEmail(domainUser.getEmail())).thenReturn(Optional.of(userEntity));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.registerUser(domainUser)
        );

        assertEquals("Email already registered.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldLoginSuccessfullyAndReturnJwt() {
        when(userRepository.findByEmail(domainUser.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("SafePassword123", "encodedHashedPassword")).thenReturn(true);
        when(jwtService.generateToken(domainUser.getEmail())).thenReturn("mocked.jwt.token");

        String token = userService.login(domainUser.getEmail(), "SafePassword123");

        assertNotNull(token);
        assertEquals("mocked.jwt.token", token);
    }

    @Test
    void shouldThrowExceptionWhenLoginEmailNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.login("unknown@email.com", "anyPassword")
        );

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldThrowExceptionWhenLoginPasswordDoesNotMatch() {
        when(userRepository.findByEmail(domainUser.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("wrongPassword", "encodedHashedPassword")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.login(domainUser.getEmail(), "wrongPassword")
        );

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(jwtService, never()).generateToken(any());
    }
}
