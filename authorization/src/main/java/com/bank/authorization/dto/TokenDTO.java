package com.bank.authorization.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private Long id;

    private String accessToken;

    private String refreshToken;

    private boolean loggedOut;

}
