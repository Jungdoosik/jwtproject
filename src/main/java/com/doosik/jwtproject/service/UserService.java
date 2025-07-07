package com.doosik.jwtproject.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.doosik.jwtproject.domain.RefreshToken;
import com.doosik.jwtproject.domain.User;
import com.doosik.jwtproject.dto.LoginResponseDto;
import com.doosik.jwtproject.dto.UserDto;
import com.doosik.jwtproject.jwt.JwtUtil;
import com.doosik.jwtproject.repository.RefreshTokenRepository;
import com.doosik.jwtproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtil jwtUtil;
	
	public void register(UserDto dto) {
		if(userRepository.findByUsername(dto.getUsername()).isPresent()){
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}
		
		User user = User.builder()
				.username(dto.getUsername())
				.password(passwordEncoder.encode(dto.getPassword()))
				.role("ROLE_USER")
				.build();
		
		userRepository.save(user);
	}
	
	//  로그인 → JWT 발급
    public LoginResponseDto login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자가 존재하지 않습니다.");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        
        //  Access + Refresh Token 발급
        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

     //  Refresh Token DB에 저장 (이미 만들어뒀다고 가정)
        refreshTokenRepository.findByUsername(username)	
            .ifPresent(refreshTokenRepository::delete);

        RefreshToken rt = RefreshToken.builder()
            .username(username)
            .token(refreshToken)
            .expiryDate(LocalDateTime.now().plusDays(7))
            .build();

        refreshTokenRepository.save(rt);

        // ✅ 두 개를 DTO로 묶어 리턴!
        return new LoginResponseDto(accessToken, refreshToken);
    }
}