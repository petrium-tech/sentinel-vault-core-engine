package com.sentinelvault.mapper;

import com.sentinelvault.controller.dto.UserRegistrationRequest;
import com.sentinelvault.controller.dto.UserResponse;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.repository.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(UserRegistrationRequest request);

    UserResponse toResponse(User domain);

    UserEntity toEntity(User domain);

    User toDomain(UserEntity entity);
}