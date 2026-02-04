package com.bank.authorization.service;

import com.bank.authorization.entity.User;
import com.bank.authorization.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String profileId) throws UsernameNotFoundException {
        try {
            Long id = Long.valueOf(profileId);   //Преобразование String в Long
            User user = userRepository.loadUserByProfileId(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities()
            );
        } catch (NumberFormatException e) {
            logger.error("Invalid profileId format: " + profileId + e.getMessage());
            throw new UsernameNotFoundException("Invalid profileId: " + profileId);
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            throw new UsernameNotFoundException("User not found with profileId: " + profileId);
        }
    }
}
