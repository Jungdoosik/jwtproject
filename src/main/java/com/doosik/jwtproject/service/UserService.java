package com.doosik.jwtproject.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.doosik.jwtproject.domain.User;
import com.doosik.jwtproject.dto.UserDto;
import com.doosik.jwtproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public void register(UserDto dto) {
		if(userRepository.findByUserName(dto.getUsername()).isPresent()){
			throw new RuntimeException("이미 존재하는 아이디입니다.");
		}
		
		User user = User.builder()
				.username(dto.getUsername())
				.password(passwordEncoder.encode(dto.getPassword()))
				.role("ROLE_USER")
				.build();
		
		userRepository.save(user);
	}
}
