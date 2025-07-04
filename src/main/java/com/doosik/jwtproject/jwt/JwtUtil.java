package com.doosik.jwtproject.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
	private String SECRET_KEY;
	
	private SecretKey key;

    @PostConstruct
    public void init() {
        // Base64 인코딩 필요 X → 길이만 충분하면 됨!
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
	
	//토큰 생성
	public String generateToken(String username) {
		long expirationTime = 1000 * 60 * 60; // 1시간
		
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
	}
	
	public String generateRefreshToken(String username) {
	    long expirationTime = 1000L * 60 * 60 * 24 * 7; // 7일
	    return Jwts.builder()
	            .setSubject(username)
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
	            .signWith(key, SignatureAlgorithm.HS512)
	            .compact();
	}
	// 토큰에서 사용자 이름 가져오기
    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        final String usernameFromToken = getUsername(token);
        return (username.equals(usernameFromToken) && !isTokenExpired(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
    
    // 필터에서 쓸 간단 버전 (유효성만)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key) // key는 이미 init()에서 준비된 SecretKey
                .build()
                .parseClaimsJws(token);
            return true; // 파싱 성공
        } catch (Exception e) {
            return false; // 서명 틀림, 만료됨 등
        }
    }
    
}
