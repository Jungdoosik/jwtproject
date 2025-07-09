package com.doosik.jwtproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.doosik.jwtproject.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest // 실제 빈 다 띄움
@AutoConfigureMockMvc // MockMvc 사용 가능하게
class AuthControllerTest {

	@Autowired
    MockMvc mockMvc; // 가짜 HTTP 요청 도구

    @Autowired
    ObjectMapper objectMapper; // JSON 직렬화/역직렬화

    @Autowired
    UserRepository userRepository; // 유저 DB 접근

    @Autowired
    PasswordEncoder passwordEncoder; // 비밀번호 암호화

}
