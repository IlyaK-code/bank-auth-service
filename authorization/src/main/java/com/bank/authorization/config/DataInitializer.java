package com.bank.authorization.config;

import com.bank.authorization.entity.Role;
import com.bank.authorization.entity.User;
import com.bank.authorization.service.UserService;
import com.bank.authorization.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataInitializer implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (userService.getUserByProfileId(111L) == null) {
            User admin = new User();
            admin.setProfileId(111L);
            admin.setPassword("admin");
            admin.setRole(Role.ADMIN);
            userService.saveUser(admin);
            log.info("Создан администратор с profileId=111");
        }

        if (userService.getUserByProfileId(222L) == null) {
            User user = new User();
            user.setProfileId(222L);
            user.setPassword("user");
            user.setRole(Role.USER);
            userService.saveUser(user);
            log.info("Создан пользователь с profileId=222");
        }
    }
}