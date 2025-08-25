package com.example.HospitalSystemBackend.Service;

import com.example.HospitalSystemBackend.Dto.request.LoginDto;
import com.example.HospitalSystemBackend.Dto.response.LoginResponseDto;
import com.example.HospitalSystemBackend.Entity.User;
import com.example.HospitalSystemBackend.Provider.JwtProvider;
import com.example.HospitalSystemBackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AdminService adminService;

    public LoginResponseDto login(String userId) {
        // 관리자 계정인지 확인
        if (adminService.isAdminUser(userId)) {
            // 관리자용 생성자 사용 (requirePassword=true, isAdmin=true)
            return new LoginResponseDto(true, true);
        }

        // 일반 사용자 로그인 처리
        Optional<User> userOptional = userRepository.findByUserIdAndJobTypeAndUseFlag(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtProvider.generateToken(userId);
            return new LoginResponseDto(token, user);
        }

        return null; // 사용자 없음
    }

    /**
     * userId로 사용자 조회 (기존 메서드 사용)
     */
    public Optional<User> findUserByIdAndJobTypeAndUseFlag(String userId) {
        return userRepository.findByUserIdAndJobTypeAndUseFlag(userId);
    }


    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }

    public String getUserIdFromToken(String token) {
        return jwtProvider.extractUserId(token);
    }

    // 관리자와 일반 사용자 모두 처리하도록 수정
    public Optional<User> getUserByToken(String token) {
        if (validateToken(token)) {
            String userId = getUserIdFromToken(token);

            // 관리자인지 확인
            if (adminService.isAdminUser(userId)) {
                // 관리자용 가상 User 객체 생성
                User adminUser = new User();
                adminUser.setUserId(userId);
                adminUser.setUserName("관리자");
                adminUser.setDeptCode("ADMIN");
                adminUser.setJobType("9"); // 관리자 jobType
                adminUser.setUseFlag("1");
                return Optional.of(adminUser);
            }

            // 일반 사용자 처리
            return userRepository.findByUserIdAndJobTypeAndUseFlag(userId);
        }
        return Optional.empty();
    }

    public List<String> getDepartments() {
        return userRepository.findDistinctDeptCodes();
    }
}