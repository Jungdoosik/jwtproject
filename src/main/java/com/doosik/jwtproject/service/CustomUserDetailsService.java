package com.doosik.jwtproject.service;

import com.doosik.jwtproject.domain.User;
import com.doosik.jwtproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 사용자 정보를 조회할 때 호출됨.
     * @param username 로그인 시 전달된 사용자명
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 사용자 찾기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 스프링 시큐리티에서 요구하는 UserDetails 타입으로 변환하여 반환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // 이미 BCrypt로 암호화되어 있음
                .roles(user.getRole().replace("ROLE_", "")) // ROLE_ 접두어 제외
                .build();
    }
}
