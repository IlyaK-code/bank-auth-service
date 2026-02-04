package com.bank.authorization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO для POST ../login
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Данные для входа")
public class LoginRequestDto {

    @ApiModelProperty(value = "Идентификатор профиля", example = "123", required = true)
    private String profile_id;
    @ApiModelProperty(value = "Пароль", example = "password", required = true)
    private String password;

    public Long getProfileId() {
        return Long.valueOf(profile_id);
    }
}