package com.bank.authorization.controller;

import com.bank.authorization.dto.AuthenticationResponseDto;
import com.bank.authorization.dto.LoginRequestDto;
import com.bank.authorization.dto.UserDTO;
import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.service.AuthenticationService;
import com.bank.authorization.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.bank.authorization.entity.Role.ADMIN;
import static com.bank.authorization.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RestController restController;

    private LoginRequestDto loginRequestDto;
    private UserDTO userDTO;
    private UserDTO user;
    private User userEntity;
    private AuthenticationResponseDto authenticationResponseDto;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        loginRequestDto = new LoginRequestDto("111", "password");
        userDTO = new UserDTO();
        userDTO.setProfileId("222");
        userDTO.setPassword("password");
        userDTO.setRole(ADMIN);

        user = new UserDTO();
        user.setProfileId("333");
        user.setPassword("password");
        user.setRole(Role.USER);

        userEntity = new User();
        userEntity.setProfileId(333L);
        userEntity.setPassword("password");
        userEntity.setRole(USER);

        authenticationResponseDto = new AuthenticationResponseDto("accessToken", "refreshToken");
    }

    @Test
    @DisplayName("Тест метода authenticate: успешная аутентификация")
    void authenticate_ShouldReturnAuthenticationResponse() {
        when(authenticationService.authenticate(loginRequestDto)).thenReturn(authenticationResponseDto);

        ResponseEntity<AuthenticationResponseDto> response = restController.authenticate(loginRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponseDto, response.getBody());
        verify(authenticationService, times(1)).authenticate(loginRequestDto);
    }

    @Test
    @DisplayName("Тест метода register: успешная регистрация")
    void register_ShouldReturnCreatedStatus() {
        when(userService.getUserByProfileId(333L)).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        ResponseEntity<String> response = restController.register(user);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(String.valueOf(user), response.getBody());
        verify(userService, times(1)).getUserByProfileId(333L);
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Тест метода register: пользователь уже существует")
    void register_ShouldReturnBadRequestWhenUserExists() {
        when(userService.getUserByProfileId(333L)).thenReturn(user);

        ResponseEntity<String> response = restController.register(user);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error. User can't be registered", response.getBody());
        verify(userService, times(1)).getUserByProfileId(333L);
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Тест метода refreshToken: успешное обновление токена")
    void refreshToken_ShouldReturnAuthenticationResponse() {
        when(authenticationService.refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(ResponseEntity.ok(authenticationResponseDto));

        ResponseEntity<AuthenticationResponseDto> response = restController.refreshToken(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponseDto, response.getBody());
        verify(authenticationService, times(1)).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}