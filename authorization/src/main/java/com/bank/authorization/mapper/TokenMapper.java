package com.bank.authorization.mapper;


import com.bank.authorization.dto.TokenDTO;
import com.bank.authorization.entity.Token;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface TokenMapper {

    @Mapping(target = "id")
    TokenDTO toDTO(Token token);
}