package com.bank.authorization.mapper;

import com.bank.authorization.dto.UserDTO;
import com.bank.authorization.entity.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(UserDTO userDTO, @MappingTarget User user);

    default Page<UserDTO> pageToDTO(Page<User> usersList) {
        return usersList.map(this::toDTO);
    }
}