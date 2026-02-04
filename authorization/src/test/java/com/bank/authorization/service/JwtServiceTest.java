package com.bank.authorization.service;

import com.bank.authorization.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
//    @Mock
//    private UserDetails userDetails;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private User user;

    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final Long accessTokenExpiration = 3600000L;
    private final Long refreshTokenExpiration = 7200000L;
    @BeforeEach
    public void init() {
        user = new User();
        user.setProfileId(123L);
        user.setPassword("password");

        setField(jwtService, "SECRET_KEY", secretKey);
        setField(jwtService, "accessTokenExpiration", accessTokenExpiration);
        setField(jwtService, "refreshTokenExpiration", refreshTokenExpiration);
    }

    @Test
    @DisplayName("Тест метода generateAccessToken: генерация access токенa")
    void generateAccessToken_ShouldReturnValidToken() {
        String accessToken = jwtService.generateAccessToken(user);
        assertNotNull(accessToken);

        String username = jwtService.extractUsername(accessToken);
        assertEquals(user.getUsername(), username);
    }

    @Test
    @DisplayName("Тест метода generateRefreshToken: генерация refresh токенa")
    void generateRefreshToken_ShouldReturnValidToken() {
        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(refreshToken);

        String username = jwtService.extractUsername(refreshToken);
        assertEquals(user.getUsername(), username);
    }

    @Test
    @DisplayName("Тест проверки валидности access токена")
    void isValidAccessToken_ShouldReturnTrueForValidToken() {
        String accessToken = jwtService.generateAccessToken(user);
        assertNotNull(accessToken);
        boolean isValid = jwtService.isValidAccessToken(accessToken, user);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Тест проверки валидности refresh токена")
    void isValidRefreshToken_ShouldReturnTrueForValidToken() {
        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(refreshToken);
        boolean isValid = jwtService.isValidRefresh(refreshToken, user);
        assertTrue(isValid);
    }
}
