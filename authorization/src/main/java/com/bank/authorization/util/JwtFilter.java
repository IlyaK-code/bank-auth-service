package com.bank.authorization.util;

import com.bank.authorization.entity.User;
import com.bank.authorization.repository.UserRepository;
import com.bank.authorization.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    public JwtFilter(JwtService jwtService, UserRepository userService) {
        this.jwtService = jwtService;
        this.userRepository = userService;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Извлекаем заголовок Authorization
        String authHeader = request.getHeader("Authorization");

        // Если заголовок отсутствует или не начинается с "Bearer ", пропускаем запрос
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();

        // Список путей, которые нужно игнорировать
        if (uri.equals("/reg")) {
            filterChain.doFilter(request, response); // Пропускаем фильтр
            return;
        }

        // Извлекаем JWT-токен из заголовка
        String token = authHeader.substring(7); // Убираем "Bearer "
        // Проверяем токен и извлекаем username (или profile_id)
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userService.loadUserByUsername(username);
            User userDetails = userRepository.getUserByProfileId(Long.parseLong(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtService.isValidAccessToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }


        filterChain.doFilter(request, response);

    }
}