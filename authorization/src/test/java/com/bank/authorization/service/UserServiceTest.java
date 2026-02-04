package com.bank.authorization.service;

import com.bank.authorization.dto.UserDTO;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.UserMapper;
import com.bank.authorization.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        user = new User();
        user.setProfileId(123L);
        user.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setProfileId("123");
        userDTO.setPassword("password");
    }

    @Test
    @DisplayName("Тест метода getAllUsers: должен вернуть список UserDTO")
    void getAllUsers_ShouldReturnListOfUserDTO() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(userDTO, result.get(0)),
                () -> verify(userRepository, times(1)).findAll(),
                () -> verify(userMapper, times(1)).toDTO(any(User.class))
        );
    }

    @Test
    @DisplayName("Тест метода getUserByProfileId: должен вернуть UserDTO по profileId")
    void getUserByProfileId_ShouldReturnUserDTO() {
        when(userRepository.getUserByProfileId(123L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.getUserByProfileId(123L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userDTO, result),
                () -> verify(userRepository, times(1)).getUserByProfileId(123L),
                () -> verify(userMapper, times(1)).toDTO(any(User.class))
        );
    }

    @Test
    @DisplayName("Тест метода getUserByProfileId: должен вернуть null, если пользователь не найден")
    void getUserByProfileId_ShouldReturnNullWhenUserNotFound() {
        when(userRepository.getUserByProfileId(123L)).thenReturn(Optional.empty());

        UserDTO result = userService.getUserByProfileId(123L);

        assertAll(
                () -> assertNull(result),
                () -> verify(userRepository, times(1)).getUserByProfileId(123L),
                () -> verify(userMapper, never()).toDTO(any(User.class))
        );
    }

    @Test
    @DisplayName("Тест метода saveUser: должен сохранить пользователя и вернуть UserDTO")
    void saveUser_ShouldSaveUserAndReturnUserDTO() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);

        UserDTO result = userService.saveUser(user);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userDTO, result),
                () -> verify(passwordEncoder, times(1)).encode("password"),
                () -> verify(userRepository, times(1)).save(user),
                () -> verify(userMapper, times(1)).toDTO(any(User.class))
        );
    }

    @Test
    @DisplayName("Тест метода saveUser: должен выбросить исключение при ошибке сохранения")
    void saveUser_ShouldThrowExceptionWhenSaveFails() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertAll(
                () -> assertThrows(RuntimeException.class, () -> userService.saveUser(user)),
                () -> verify(passwordEncoder, times(1)).encode("password"),
                () -> verify(userRepository, times(1)).save(user),
                () -> verify(userMapper, never()).toDTO(any(User.class))
        );
    }
}