package com.sentinelvault.mapper;

import com.sentinelvault.domain.model.User;
import com.sentinelvault.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);
}
