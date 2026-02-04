package com.bank.authorization.service;

import com.bank.authorization.dto.AuthenticationResponseDto;
import com.bank.authorization.dto.LoginRequestDto;
import com.bank.authorization.entity.Token;
import com.bank.authorization.entity.User;
import com.bank.authorization.repository.TokenRepository;
import com.bank.authorization.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private LoginRequestDto loginRequestDto;
    private Token token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setProfileId(123L);
        user.setPassword("password");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setProfile_id("123");
        loginRequestDto.setPassword("password");

        token = new Token();
        token.setAccessToken("accessToken");
        token.setRefreshToken("refreshToken");
        token.setLoggedOut(false);
        token.setUser(user);
    }

    @Test
    @DisplayName("Тест метода authenticate: успешная аутентификация и генерация токенов")
    void authenticate_ShouldReturnAuthenticationResponseDto() {
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.getUserByProfileId(123L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
        when(tokenRepository.findAllAccessTokenByUser(user.getId())).thenReturn(Collections.emptyList());

        AuthenticationResponseDto response = authenticationService.authenticate(loginRequestDto);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("accessToken", response.getAccessToken()),
                () -> assertEquals("refreshToken", response.getRefreshToken()),
                () -> verify(authenticationManager, times(1))
                        .authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(userRepository, times(1)).getUserByProfileId(123L),
                () -> verify(jwtService, times(1)).generateAccessToken(user),
                () -> verify(jwtService, times(1)).generateRefreshToken(user),
                () -> verify(tokenRepository, times(1)).findAllAccessTokenByUser(user.getId()),
                () -> verify(tokenRepository, times(1)).save(any(Token.class))
        );

    }

    @Test
    @DisplayName("Тест метода authenticate: аутентификация с неверными учетными данными")
    void authenticate_ShouldThrowExceptionWhenInvalidCredentials() {
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertAll(
                () -> assertThrows(RuntimeException.class,
                        () -> authenticationService.authenticate(loginRequestDto)),
                () -> verify(authenticationManager, times(1))
                        .authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(userRepository, never()).getUserByProfileId(anyLong()),
                () -> verify(jwtService, never()).generateAccessToken(any(User.class)),
                () -> verify(jwtService, never()).generateRefreshToken(any(User.class)),
                () -> verify(tokenRepository, never()).findAllAccessTokenByUser(anyLong()),
                () -> verify(tokenRepository, never()).save(any(Token.class))
        );
    }

    @Test
    @DisplayName("Тест метода authenticate: пользователь не найден")
    void authenticate_ShouldReturnNullWhenUserNotFound() {

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.getUserByProfileId(123L)).thenReturn(Optional.empty());

        AuthenticationResponseDto response = authenticationService.authenticate(loginRequestDto);

        assertAll(
                () -> assertNull(response),
                () -> verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(userRepository, times(1)).getUserByProfileId(123L),
                () -> verify(jwtService, never()).generateAccessToken(any(User.class)),
                () -> verify(jwtService, never()).generateRefreshToken(any(User.class)),
                () -> verify(tokenRepository, never()).findAllAccessTokenByUser(anyLong()),
                () -> verify(tokenRepository, never()).save(any(Token.class))
        );
    }

    @Test
    @DisplayName("Тест метода refreshToken: успешное обновление токенов")
    void refreshToken_ShouldReturnNewTokens() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer refreshToken");
        when(jwtService.extractUsername("refreshToken")).thenReturn("123");
        when(userRepository.getUserByProfileId(123L)).thenReturn(Optional.of(user));
        when(jwtService.isValidRefresh("refreshToken", user)).thenReturn(true);

        when(jwtService.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("newRefreshToken");

        when(tokenRepository.findAllAccessTokenByUser(user.getId())).thenReturn(Collections.emptyList());

        ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request, response);

        assertAll(
                () -> assertNotNull(responseEntity),
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals("newAccessToken", responseEntity.getBody().getAccessToken()),
                () -> assertEquals("newRefreshToken", responseEntity.getBody().getRefreshToken()),
                () -> verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION),
                () -> verify(jwtService, times(1)).extractUsername("refreshToken"),
                () -> verify(userRepository, times(1)).getUserByProfileId(123L),
                () -> verify(jwtService, times(1)).isValidRefresh("refreshToken", user),
                () -> verify(jwtService, times(1)).generateAccessToken(user),
                () -> verify(jwtService, times(1)).generateRefreshToken(user),
                () -> verify(tokenRepository, times(1)).findAllAccessTokenByUser(user.getId()),
                () -> verify(tokenRepository, times(1)).save(any(Token.class))
        );
    }

    @Test
    @DisplayName("Тест метода refreshToken: неверный заголовок Authorization")
    void refreshToken_ShouldReturnUnauthorizedWhenInvalidHeader() {
        // Мокируем HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Мокируем заголовок Authorization (неверный формат)
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");

        // Вызов тестируемого метода
        ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request, response);

        // Проверка результата
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

        // Проверка вызова методов
        verify(request, times(1)).getHeader(HttpHeaders.AUTHORIZATION);
        verify(jwtService, never()).extractUsername(anyString());
        verify(userRepository, never()).getUserByProfileId(anyLong());
        verify(jwtService, never()).isValidRefresh(anyString(), any(User.class));
        verify(jwtService, never()).generateAccessToken(any(User.class));
        verify(jwtService, never()).generateRefreshToken(any(User.class));
        verify(tokenRepository, never()).findAllAccessTokenByUser(anyLong());
        verify(tokenRepository, never()).save(any(Token.class));
    }
}