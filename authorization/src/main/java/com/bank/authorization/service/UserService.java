package com.bank.authorization.service;

import com.bank.authorization.dto.UserDTO;
import com.bank.authorization.entity.User;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO saveUser(User user);

    UserDTO getUserByProfileId(Long profileId);

}
