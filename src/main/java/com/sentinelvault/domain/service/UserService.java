package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.mapper.UserMapper;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.UserEntity;
import com.sentinelvault.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Transactional
    public User registerUser(User user) {
        // 1. Valida se o e-mail já está cadastrado no banco
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException("Email already registered.");
        }

        // 2. Criptografa a senha do domínio usando o BCryptEncoder
        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        // 3. Converte o modelo de domínio para a entidade JPA física do banco
        UserEntity entity = userMapper.toEntity(user);
        entity.setPasswordHash(encryptedPassword); // Define a senha criptografada na coluna física

        // 4. Salva no PostgreSQL do Docker
        UserEntity savedEntity = userRepository.save(entity);

        // 5. Retorna o domínio salvo de volta para o controlador
        return userMapper.toDomain(savedEntity);
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Invalid email or password.");
        }

        // Emite um JWT real e seguro assinado criptograficamente!
        return jwtService.generateToken(user.getEmail());
    }
}