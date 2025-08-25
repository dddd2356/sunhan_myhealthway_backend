package com.example.HospitalSystemBackend.Security;

import com.example.HospitalSystemBackend.Entity.User;
import com.example.HospitalSystemBackend.Provider.JwtProvider;
import com.example.HospitalSystemBackend.Service.AdminService;
import com.example.HospitalSystemBackend.Service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws java.io.IOException, ServletException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtProvider.validateToken(token)) {
                    String userId = jwtProvider.extractUserId(token);

                    // 관리자인지 확인
                    if (adminService.isAdminUser(userId)) {
                        // 관리자용 User 객체 생성 (실제 DB에서 조회하거나 가상 객체 생성)
                        User adminUser = createAdminUser(userId);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(adminUser, null,
                                        Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        // 일반 사용자 처리 - User 엔티티를 Principal로 설정
                        Optional<User> userOpt = authService.getUserByToken(token);
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(user, null,
                                            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        } else {
                            // User 객체를 찾지 못한 경우, userId로 조회 시도
                            Optional<User> userByIdOpt = authService.findUserByIdAndJobTypeAndUseFlag(userId);
                            if (userByIdOpt.isPresent()) {
                                User user = userByIdOpt.get();
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(user, null,
                                                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("JWT 토큰 처리 오류: ", e);
            }
        }
        // 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

    /**
     * 관리자용 User 객체 생성
     * 실제로는 DB에서 조회하거나, 관리자 정보를 담은 User 객체를 생성
     */
    private User createAdminUser(String userId) {
        User adminUser = new User();
        adminUser.setUserId(userId);
        adminUser.setUserName("관리자");
        adminUser.setDeptCode("ADMIN"); // 관리자는 ADMIN 부서로 설정
        return adminUser;
    }
}