package com.example.HospitalSystemBackend.Dto.response;

import com.example.HospitalSystemBackend.Entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {
    private String token;
    private User user;
    private boolean requirePassword = false; // 관리자 계정의 경우 비밀번호 추가 입력 필요
    private boolean isAdmin = false; // 관리자 계정 여부
    private String userId; // 추가
    private boolean success; // 추가
    private String message; // 추가

    // 일반 사용자용 생성자
    public LoginResponseDto(String token, User user) {
        this.token = token;
        this.user = user;
        this.requirePassword = false;
        this.isAdmin = false;
    }

    // 관리자용 생성자
    public LoginResponseDto(boolean requirePassword, boolean isAdmin) {
        this.requirePassword = requirePassword;
        this.isAdmin = isAdmin;
    }

    // 관리자 로그인 성공용 생성자 (새로 추가)
    public LoginResponseDto(String token, String userId, boolean isAdmin) {
        this.token = token;
        this.userId = userId;
        this.isAdmin = isAdmin;
        this.success = true;
        this.message = "관리자 로그인 성공";
        this.requirePassword = false;
    }
}