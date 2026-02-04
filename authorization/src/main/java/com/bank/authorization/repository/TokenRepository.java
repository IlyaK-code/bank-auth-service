package com.bank.authorization.repository;

import com.bank.authorization.entity.Token;
import com.bank.authorization.entity.User;
import com.bank.authorization.service.JwtServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
            SELECT t FROM Token t inner join User u
            on t.user.id = u.id
            where t.user.id = :userId and t.loggedOut = false
            """)
    List<Token> findAllAccessTokenByUser(@Param("userId") Long userId);

    Optional<Token> findByAccessToken(String accessToken);

    default  Token save(User user) {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        Token token = new Token();
        token.setUser(user);
        token.setAccessToken(jwtService.generateAccessToken(user));
        token.setRefreshToken(jwtService.generateRefreshToken(user));
        System.out.println("Token saved successfully" + token);
        return save(token);
    }

}