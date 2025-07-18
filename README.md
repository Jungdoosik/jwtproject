# 🔒 Spring Boot JWT 인증 프로젝트

이 프로젝트는 Spring Boot 기반으로 JWT 인증 흐름을 구현한 예제입니다.
Access Token + Refresh Token + 권한 관리 + Swagger 연동 + 테스트 코드까지 모두 포함됩니다.

---

## 📌 프로젝트 구조

| 기능 | 설명 |
|------|------|
| ✅ 회원가입 | `/api/auth/register` |
| ✅ 로그인 | `/api/auth/login` - JWT 발급 (Access + Refresh) |
| ✅ 내 정보 조회 | `/api/user/me` |
| ✅ Access Token 갱신 | `/api/auth/refresh` |
| ✅ 로그아웃 | `/api/auth/logout` - Refresh Token 무효화 |
| ✅ Swagger 연동 | `/swagger-ui.html` |
| ✅ 권한 관리 | ROLE_USER, ROLE_ADMIN |
| ✅ 통합 테스트 | MockMvc 사용 |

---

## 🧩 사용 기술 스택

- Spring Boot 3.x
- Spring Security
- JWT (io.jsonwebtoken.JJJWT)
- H2 Database (테스트용)
- JPA/Hibernate (jpa는 인터페이스, Hibernate는 엔진이라 결론적으로 동작시키는건 Hibernate)
- Swagger (SpringDoc OpenAPI)
- JUnit5 + MockMvc

---

## ⚙️ JWT 흐름 설명

1️⃣ **회원가입**
- `/api/auth/register`  
- 사용자 정보를 DB에 저장  
- 비밀번호는 BCrypt로 안전하게 암호화

2️⃣ **로그인**
- `/api/auth/login`
- 아이디/비밀번호 확인 후
- Access Token(1시간) + Refresh Token(7일) 발급
- Refresh Token은 DB에 저장 (무효화 가능)

3️⃣ **요청 인증**
- JWT는 HTTP Header `Authorization: Bearer {AccessToken}`
- `JwtAuthenticationFilter`가 요청마다 JWT를 검증 → `SecurityContext`에 사용자 정보 등록

4️⃣ **내 정보 조회**
- `/api/user/me`
- JWT 인증이 성공하면 현재 로그인한 사용자 정보 반환

5️⃣ **Access Token 갱신**
- `/api/auth/refresh`
- 만료된 Access Token 대신 유효한 Refresh Token으로 새로 발급

6️⃣ **로그아웃**
- `/api/auth/logout`
- 서버에 저장된 Refresh Token 삭제 → 탈취 시에도 무효화

7️⃣ **권한 관리**
- `SecurityConfig`에서 URL 별로 `permitAll()` 또는 `authenticated()`
- 사용자 역할(ROLE_USER, ROLE_ADMIN)에 따라 접근 제어 가능

---

## ✅ Swagger 연동

- `/swagger-ui.html` 접속  
- `Authorize` 버튼 클릭 → `Bearer {AccessToken}` 입력  
- 인증이 필요한 API 테스트 가능

---

## ✅ 실행 방법

```bash
# 1. clone
git clone https://github.com/Jungdoosik/jwtproject.git

# 2. build
./gradlew build

# 3. run
./gradlew bootRun

# 4. Swagger 접속
http://localhost:8080/swagger-ui.html
