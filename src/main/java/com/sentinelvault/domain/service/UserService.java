package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.mapper.UserMapper;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email address is already registered.");
        }

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        UserEntity entityToSave = userMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entityToSave);

        return userMapper.toDomain(savedEntity);
    }

    @Transactional(readOnly = true)
    public String authenticateUser(String email, String password) {
        // Busca o usuário no banco de dados do Docker pelo e-mail
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password."));

        // Compara de forma segura a senha aberta enviada com a hash criptografada
        if (!passwordEncoder.matches(password, entity.getPasswordHash())) {
            throw new BusinessException("Invalid email or password.");
        }

        // Retorna um token simulado por enquanto (nas próximas etapas geraremos um JWT real)
        return "mock-jwt-token-for-" + entity.getEmail();
    }
}