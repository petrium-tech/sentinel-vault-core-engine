package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.domain.model.User;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final ConcurrentHashMap<String, User> memoryDatabase = new ConcurrentHashMap<>();

    public User registerUser(User user) {
        if (memoryDatabase.containsKey(user.getEmail())) {
            throw new BusinessException("Email address is already registered.");
        }

        user.setId(UUID.randomUUID());
        memoryDatabase.put(user.getEmail(), user);

        return user;
    }
}