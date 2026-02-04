package com.bank.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponseDto {

    private final String accessToken;

    private final String refreshToken;

}