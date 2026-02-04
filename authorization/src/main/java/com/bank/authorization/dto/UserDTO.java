package com.bank.authorization.dto;

import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

// DTO класса User

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String profileId;

    private Role role;

    private String password;

    private List<TokenDTO> tokens;

}
