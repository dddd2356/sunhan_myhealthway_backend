package com.example.HospitalSystemBackend.Controller;

import com.example.HospitalSystemBackend.Dto.request.LoginDto;
import com.example.HospitalSystemBackend.Dto.response.LoginResponseDto;
import com.example.HospitalSystemBackend.Entity.User;
import com.example.HospitalSystemBackend.Provider.JwtProvider;
import com.example.HospitalSystemBackend.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            LoginResponseDto result = authService.login(loginDto.getUserId());

            if (result == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "사용자를 찾을 수 없습니다");
                return ResponseEntity.badRequest().body(error);
            }

            // 관리자 계정의 경우 비밀번호 입력 필요 응답
            if (result.isRequirePassword()) {
                Map<String, Object> response = new HashMap<>();
                response.put("requirePassword", true);
                response.put("isAdmin", true);
                response.put("userId", loginDto.getUserId());
                return ResponseEntity.ok(response);
            }

            // 일반 사용자 로그인 성공 응답
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "로그인 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = jwtProvider.extractTokenFromHeader(authHeader);

        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid token format");
        }

        Optional<User> user = authService.getUserByToken(token);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<List<String>> getDepartments() {
        List<String> departments = authService.getDepartments();
        return ResponseEntity.ok(departments);
    }
}