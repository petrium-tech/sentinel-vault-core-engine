package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.mapper.UserMapper;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email address is already registered.");
        }

        UserEntity entityToSave = userMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(entityToSave);

        return userMapper.toDomain(savedEntity);
    }
}