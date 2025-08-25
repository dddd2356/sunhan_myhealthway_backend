package com.example.HospitalSystemBackend.Security;

import com.example.HospitalSystemBackend.Entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final User principal;

    public JwtAuthenticationToken(User principal) {
        super(Collections.emptyList());   // 빈 권한 목록으로 초기화
        this.principal = principal;
        setAuthenticated(true);             // 이미 토큰 검증을 마쳤으므로 true로 설정
    }

    @Override
    public Object getCredentials() {
        return null;                        // JWT 자체를 자격증명으로 쓸 것이므로 별도 크리덴셜은 없음
    }

    @Override
    public Object getPrincipal() {
        return principal;                   // Spring Security가 참조할 사용자 객체
    }
}