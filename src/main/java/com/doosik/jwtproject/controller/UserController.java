package com.doosik.jwtproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doosik.jwtproject.domain.User;
import com.doosik.jwtproject.dto.UserDto;
import com.doosik.jwtproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserRepository userRepository;
	
	//로그인한 사용자의 정보 조회 api
	@GetMapping("/myInfo")
	public ResponseEntity<UserDto> getMyInfo(Authentication authentication){
		String userName = authentication.getName();
			
		// SecurityContext에서 현재 로그인한 사용자의 username 꺼내기
        String username = authentication.getName();

        // DB에서 username으로 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 사용자 정보를 DTO로 만들어서 클라이언트에 전달
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole());
			
		return ResponseEntity.ok(userDto);
		
	}
}
