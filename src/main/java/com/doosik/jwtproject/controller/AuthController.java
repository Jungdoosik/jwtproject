package com.doosik.jwtproject.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doosik.jwtproject.domain.RefreshToken;
import com.doosik.jwtproject.dto.LoginResponseDto;
import com.doosik.jwtproject.dto.RefreshRequestDto;
import com.doosik.jwtproject.dto.TokenResponseDto;
import com.doosik.jwtproject.dto.UserDto;
import com.doosik.jwtproject.jwt.JwtUtil;
import com.doosik.jwtproject.repository.RefreshTokenRepository;
import com.doosik.jwtproject.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final UserService userService;
	private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

	@PostMapping("/signup")
	public ResponseEntity<String> register(@RequestBody UserDto userDto) {
		userService.register(userDto);
		return ResponseEntity.ok("회원가입 성공!");
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody UserDto userDto) {
		LoginResponseDto tokens = userService.login(userDto.getUsername(), userDto.getPassword());
		return ResponseEntity.ok(tokens);
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshRequestDto request) {
		String refreshToken = request.getRefreshToken();

		RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new RuntimeException("유효하지 않은 Refresh Token"));

		if (savedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Refresh Token 만료됨");
		}

		String newAccessToken = jwtUtil.generateToken(savedToken.getUsername());

		return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
	}
}
