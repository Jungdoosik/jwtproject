package com.doosik.jwtproject;

import static org.assertj.core.api.Assertions.assertThat; // 결과 검증
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // POST 요청 빌더
// 결과검증 DSL
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach; // 테스트마다 실행
import org.junit.jupiter.api.DisplayName; // 테스트 이름
import org.junit.jupiter.api.Test; // 테스트 어노테이션
import org.springframework.beans.factory.annotation.Autowired; // 의존성 주입
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // MockMvc 자동 설정
import org.springframework.boot.test.context.SpringBootTest; // 스프링 통합테스트
import org.springframework.http.MediaType; // 요청 타입
import org.springframework.security.crypto.password.PasswordEncoder; // 비밀번호 암호화
import org.springframework.test.web.servlet.MockMvc; // MockMvc: HTTP 요청 시뮬레이션

import com.doosik.jwtproject.domain.User; // 유저 엔티티
import com.doosik.jwtproject.dto.LoginResponseDto;
import com.doosik.jwtproject.dto.RefreshRequestDto;
import com.doosik.jwtproject.repository.RefreshTokenRepository;
import com.doosik.jwtproject.repository.UserRepository; // 유저 저장소
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 직렬화 도구

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
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    
    private String accessToken; // 👉 테스트용 AccessToken 저장
    
    private String refreshToken;

    @BeforeEach // 각 테스트 실행 전에 실행됨
    void setUp() {
        userRepository.deleteAll(); // 기존 유저 다 삭제 (테스트 깨끗하게)

        // 테스트용 사용자 등록
        User user = User.builder()
                .username("testuser") // 아이디
                .password(passwordEncoder.encode("1234")) // 비밀번호 암호화해서 저장
                .role("ROLE_USER") // 기본 권한
                .build();

        userRepository.save(user); // DB에 저장
    }
    
    @Test
    @DisplayName("로그인 성공 시 Access + Refresh Token 발급") // 테스트 이름
    void loginSuccess() throws Exception {
        // given: 로그인 요청 DTO 준비
    	User request = new User();
        request.setUsername("testuser");
        request.setPassword("1234");

        // when & then: MockMvc로 POST /api/auth/login 요청 보내고 결과 검증
        String result = mockMvc.perform(
                        post("/api/auth/login") // POST 요청
                                .contentType(MediaType.APPLICATION_JSON) // JSON 타입으로 보냄
                                .content(objectMapper.writeValueAsString(request)) // DTO -> JSON
                )
                .andExpect(status().isOk()) // HTTP 200 OK 기대
                .andExpect(jsonPath("$.accessToken").exists()) // JSON에 accessToken 필드 존재?
                .andExpect(jsonPath("$.refreshToken").exists()) // JSON에 refreshToken 필드 존재?
                .andReturn()
                .getResponse()
                .getContentAsString(); // 응답 JSON 문자열로 받음

        // 응답 JSON -> LoginResponse 객체로 역직렬화
        LoginResponseDto response = objectMapper.readValue(result, LoginResponseDto.class);

        // 실제로 값이 잘 들어왔는지 검증
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }
    
//    @BeforeEach
//    void setUp() throws Exception {
//        userRepository.deleteAll(); // 👉 DB 초기화
//
//        // 👉 테스트용 사용자 저장
//        User user = User.builder()
//                .username("testuser")
//                .password(passwordEncoder.encode("1234"))
//                .role("ROLE_USER")
//                .build();
//
//        userRepository.save(user);
//
//        // 👉 로그인 요청해서 JWT AccessToken 미리 받아두기
//        User request = new User();
//        request.setUsername("testuser");
//        request.setPassword("1234");
//
//        String result = mockMvc.perform(
//                        post("/api/auth/login") // 👉 POST /api/auth/login 요청
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                )
//                .andExpect(status().isOk()) // 👉 200 OK 기대
//                .andReturn()
//                .getResponse()
//                .getContentAsString(); // 👉 JSON 응답 문자열로 받기
//
//        // 👉 응답 JSON → Map으로 파싱해서 accessToken 꺼내기
//        accessToken = objectMapper.readTree(result).get("accessToken").asText();
//    }
//    
//    @Test
//    @DisplayName("JWT 인증 시 현재 사용자 정보 반환")
//    void getMyInfo() throws Exception {
//        //  when: GET /api/user/me 요청 (Authorization 헤더에 Bearer 붙임)
//        mockMvc.perform(
//                        get("/api/user/me") // GET 요청
//                                .header("Authorization", "Bearer " + accessToken) // 👉 JWT 인증
//                                .accept(MediaType.APPLICATION_JSON) // 👉 JSON 응답 기대
//                )
//                .andExpect(status().isOk()) // 200 OK 기대
//                .andExpect(jsonPath("$.username").value("testuser")) // 👉 username 값 검증
//                .andExpect(jsonPath("$.role").value("ROLE_USER")); // 👉 role 값 검증
//    }
    
//    @BeforeEach
//    void setUp() throws Exception {
//        userRepository.deleteAll();
//        refreshTokenRepository.deleteAll(); // 👉 RefreshToken 저장소도 깨끗하게
//
//        // 👉 테스트 사용자 저장
//        User user = User.builder()
//                .username("testuser")
//                .password(passwordEncoder.encode("1234"))
//                .role("ROLE_USER")
//                .build();
//        userRepository.save(user);
//
//        // 👉 로그인 요청으로 RefreshToken 발급받기
//        User request = new User();
//        request.setUsername("testuser");
//        request.setPassword("1234");
//
//        String result = mockMvc.perform(
//                        post("/api/auth/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request))
//                )
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        refreshToken = objectMapper.readTree(result).get("refreshToken").asText();
//
//        // 👉 DB에 RefreshToken 존재 확인 (실제 저장됨)
//        boolean exists = refreshTokenRepository.findByToken(refreshToken).isPresent();
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    @DisplayName("로그아웃 성공 시 Refresh Token 삭제")
//    void logoutSuccess() throws Exception {
//        // 👉 로그아웃 요청 바디 준비
//    	RefreshRequestDto logoutRequest = new RefreshRequestDto();
//        logoutRequest.setRefreshToken(refreshToken);
//
//        // 👉 POST /api/auth/logout 요청
//        mockMvc.perform(
//                        post("/api/auth/logout")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(logoutRequest))
//                )
//                .andExpect(status().isOk());
//
//        // 👉 DB에 RefreshToken이 삭제됐는지 확인
//        boolean existsAfterLogout = refreshTokenRepository.findByToken(refreshToken).isPresent();
//        assertThat(existsAfterLogout).isFalse();
//    }
}
