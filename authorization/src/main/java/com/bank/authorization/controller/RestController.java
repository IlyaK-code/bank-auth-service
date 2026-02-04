package com.bank.authorization.controller;

import com.bank.authorization.dto.AuthenticationResponseDto;
import com.bank.authorization.dto.LoginRequestDto;
import com.bank.authorization.dto.UserDTO;
import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.service.AuthenticationService;
import com.bank.authorization.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public RestController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя", description = "Возвращает токен доступа и обновления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/reg")
    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя с ролью USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Пользователь уже существует")
    })
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        User user = new User();

        user.setRole(Role.USER);
        user.setProfileId(Long.valueOf(userDTO.getProfileId()));
        user.setPassword(userDTO.getPassword());

        if (userService.getUserByProfileId(Long.valueOf(userDTO.getProfileId())) == null) {
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(userDTO));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error. User can't be registered");
        }
    }

    @PostMapping("/api/authorization/refresh_token")
    @Operation(summary = "Обновление токена доступа", description = "Возвращает новый токен доступа и обновления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлен"),
            @ApiResponse(responseCode = "401", description = "Недействительный токен обновления")
    })
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        return authenticationService.refreshToken(request, response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить всех пользователей (только для ADMIN)", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public ResponseEntity<List<UserDTO>> getAllUsersForAdmin() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
