package com.doosik.jwtproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doosik.jwtproject.dto.UserDto;
import com.doosik.jwtproject.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> register(@RequestBody UserDto userDto){
		userService.register(userDto);
		return ResponseEntity.ok("회원가입 성공!");
	}
	
	@PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        String token = userService.login(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.ok(token);
    }
}
