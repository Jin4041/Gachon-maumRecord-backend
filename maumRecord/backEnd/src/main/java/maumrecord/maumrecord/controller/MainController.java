package maumrecord.maumrecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.User;
import maumrecord.maumrecord.dto.LoginRequest;
import maumrecord.maumrecord.dto.UserRequest;
import maumrecord.maumrecord.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "비로그인 회원 관련 기능")
public class MainController {
    private final UserService userService;

    @PostMapping(value="/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<String> signUp(@RequestBody UserRequest request){
        userService.signUp(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping(value = "/login")
    @Operation(summary = "로그인")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response){
        String refreshToken=userService.login(request);
        String accessToken = userService.createNewAccessToken(refreshToken);
        // access token 쿠키 설정
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30); // 30분(쿠키 만료시간은 토큰과 동일하게 설정)

        // refresh token 쿠키 설정
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24); // 1일

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("로그인 성공");
    }

    @GetMapping(value = "/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // accessToken 삭제
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setMaxAge(0); // 즉시 만료
        accessCookie.setPath("/"); // 설정한 Path와 동일해야 삭제됨
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);

        // refreshToken 삭제
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("로그아웃 완료");
    }

    @PostMapping("/newToken")
    public ResponseEntity<String> createAccessToken(@CookieValue("refreshToken") String refreshToken,
                                                    HttpServletResponse response) {
        String newAccessToken = userService.createNewAccessToken(refreshToken);

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30);

        response.addCookie(accessCookie);

        return ResponseEntity.ok("새로운 Access Token 발급 완료");
    }

    //todo: 테스트용
    @GetMapping(value = "/users")
    @Operation(summary = "회원목록 조회- 테스트용")
    public List<User> list() {
        return userService.findUsers();
    }

}
