package com.sentinelvault.mapper;

import com.sentinelvault.controller.dto.UserRegistrationRequest;
import com.sentinelvault.controller.dto.UserResponse;
import com.sentinelvault.domain.model.User;
import com.sentinelvault.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toDomain(UserRegistrationRequest request);

    UserResponse toResponse(User domain);

    @Mapping(source = "password", target = "passwordHash")
    UserEntity toEntity(User domain);

    @Mapping(source = "passwordHash", target = "password")
    User toDomain(UserEntity entity);
}
