package com.bank.authorization.service;

import com.bank.authorization.dto.UserDTO;
import com.bank.authorization.entity.Token;
import com.bank.authorization.entity.User;
import com.bank.authorization.mapper.UserMapper;
import com.bank.authorization.repository.TokenRepository;
import com.bank.authorization.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository
            , PasswordEncoder passwordEncoder
            , UserMapper userMapper
            , JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByProfileId(Long profileId) {
        return userMapper.toDTO(userRepository.getUserByProfileId(profileId).orElse(null));
    }

    @Transactional
    @Override
    public UserDTO saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            return userMapper.toDTO(userRepository.save(user));
        } catch (Throwable e) {
            log.error("Не удалось создать пользователя: " + user);
            throw e;
        }
    }
}
