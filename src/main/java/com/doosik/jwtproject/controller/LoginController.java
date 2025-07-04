package com.doosik.jwtproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class LoginController {
	
	@GetMapping("/hello")
    public String hello() {
        return "인증 성공! 보호된 API 접근 완료!";
    }

}
