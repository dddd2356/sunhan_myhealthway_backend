package com.example.HospitalSystemBackend.Controller;

import com.example.HospitalSystemBackend.Dto.request.WebViewerRequestDto;
import com.example.HospitalSystemBackend.Provider.JwtProvider;
import com.example.HospitalSystemBackend.Repository.UserRepository;
import com.example.HospitalSystemBackend.Service.AdminService;
import com.example.HospitalSystemBackend.Service.WebViewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/webviewer")
public class WebViewerController {

    @Autowired
    private WebViewerService webViewerService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/open")
    public ResponseEntity<?> openWebViewer(
            @RequestBody WebViewerRequestDto request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 토큰 검증 및 권한 확인
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (!jwtProvider.validateToken(token)) {
                    return ResponseEntity.status(401).body(Map.of("error", "유효하지 않은 토큰입니다"));
                }

                String userId = jwtProvider.extractUserId(token);

                // 관리자이거나 일반 사용자인지 확인
                boolean isAuthorized = adminService.isAdminUser(userId) ||
                        userRepository.findByUserIdAndJobTypeAndUseFlag(userId).isPresent();

                if (!isAuthorized) {
                    return ResponseEntity.status(403).body(Map.of("error", "접근 권한이 없습니다"));
                }
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "인증 토큰이 필요합니다"));
            }

            // 서비스로부터 최종 웹뷰어 URL을 받음
            String finalUrl = webViewerService.authenticateAndGetFinalUrl(request);

            Map<String, String> result = new HashMap<>();
            result.put("webViewerUrl", finalUrl);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);

        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "웹뷰어 연결 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(err);
        }
    }
}