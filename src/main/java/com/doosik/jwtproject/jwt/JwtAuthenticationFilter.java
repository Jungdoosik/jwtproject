package com.doosik.jwtproject.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.doosik.jwtproject.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1️. 헤더에서 Authorization 꺼내기
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // 2️. 헤더가 Bearer 토큰 형식인지 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " 이후부터 잘라냄
            username = jwtUtil.getUsername(token);
        }

        // 3️. username 있고, 아직 인증 안된 상태라면
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // 4️. JWT 유효성 검사
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // 5️. 인증 객체 생성
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6️. SecurityContextHolder에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7️. 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }
}