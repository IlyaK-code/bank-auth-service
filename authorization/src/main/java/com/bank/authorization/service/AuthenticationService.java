package com.bank.authorization.service;

import com.bank.authorization.dto.AuthenticationResponseDto;
import com.bank.authorization.dto.LoginRequestDto;
import com.bank.authorization.entity.Token;
import com.bank.authorization.entity.User;
import com.bank.authorization.repository.TokenRepository;
import com.bank.authorization.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;


    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,
                                 TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    public AuthenticationResponseDto authenticate(LoginRequestDto request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getProfileId(),
                            request.getPassword()
                    )
            );
            System.out.println("Authentication successful");
        } catch (BadCredentialsException e) {
            System.err.println("Authentication failed: Invalid credentials");
            throw new RuntimeException("Invalid credentials", e);
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }

        User user;
        try {
            user = userRepository.getUserByProfileId(Long.valueOf(request.getProfile_id()))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            System.err.println("Found user: " + user);
        } catch (UsernameNotFoundException e) {
            user = null;
            System.err.println(e.getMessage());
        }

        if (user != null) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            System.out.println("Access Token: " + accessToken);
            System.out.println("Refresh Token: " + refreshToken);

            revokeAllToken(user);
            saveUserToken(accessToken, refreshToken, user);

            return new AuthenticationResponseDto(accessToken, refreshToken);
        }


        return null;
    }

    /// аннулирует все действительные токены
    private void revokeAllToken(User user) {

        List<Token> validTokens = tokenRepository.findAllAccessTokenByUser(user.getId());

        if (!validTokens.isEmpty()) {
            validTokens.forEach(t ->
                t.setLoggedOut(true));
        }

        tokenRepository.saveAll(validTokens);
    }

    /// сохраняет токены для пользователя
    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }

    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.getUserByProfileId(Long.valueOf(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtService.isValidRefresh(token, user)) {

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);

            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}