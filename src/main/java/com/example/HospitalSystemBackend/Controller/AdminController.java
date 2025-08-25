package com.example.HospitalSystemBackend.Controller;

import com.example.HospitalSystemBackend.Dto.response.LoginResponseDto;
import com.example.HospitalSystemBackend.Entity.MyHealthWay;
import com.example.HospitalSystemBackend.Repository.MyHealthWayRepository;
import com.example.HospitalSystemBackend.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String password = request.get("password");

            // AdminService의 새로운 adminLogin 메서드 사용
            LoginResponseDto result = adminService.adminLogin(userId, password);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 관리자 설정 조회
     */
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings() {
        try {
            Map<String, Object> settings = adminService.getAdminSettings();
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "설정을 가져오는데 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * URL 업데이트
     */
    @PostMapping("/settings/url")
    public ResponseEntity<String> updateUrl(@RequestBody Map<String, String> request) {
        try {
            String newUrl = request.get("url");
            if (newUrl == null || newUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("URL이 필요합니다.");
            }

            adminService.updateThirdPartyAuthUrl(newUrl);
            return ResponseEntity.ok("URL이 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("URL 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Client ID 업데이트
     */
    @PostMapping("/settings/client-id")
    public ResponseEntity<String> updateClientId(@RequestBody Map<String, String> request) {
        try {
            String clientId = request.get("clientId");
            if (clientId == null || clientId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Client ID가 필요합니다.");
            }

            adminService.updateClientId(clientId);
            return ResponseEntity.ok("Client ID가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Client ID 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Client Secret 업데이트
     */
    @PostMapping("/settings/client-secret")
    public ResponseEntity<String> updateClientSecret(@RequestBody Map<String, String> request) {
        try {
            String clientSecret = request.get("clientSecret");
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Client Secret이 필요합니다.");
            }

            adminService.updateClientSecret(clientSecret);
            return ResponseEntity.ok("Client Secret이 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Client Secret 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Utilization Service No 업데이트
     */
    @PostMapping("/settings/utilization-service-no")
    public ResponseEntity<String> updateUtilizationServiceNo(@RequestBody Map<String, String> request) {
        try {
            String utilizationServiceNo = request.get("utilizationServiceNo");
            if (utilizationServiceNo == null || utilizationServiceNo.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Utilization Service No가 필요합니다.");
            }

            adminService.updateUtilizationServiceNo(utilizationServiceNo);
            return ResponseEntity.ok("Utilization Service No가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Utilization Service No 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Institution Code 업데이트
     */
    @PostMapping("/settings/institution-code")
    public ResponseEntity<String> updateInstitutionCode(@RequestBody Map<String, String> request) {
        try {
            String institutionCode = request.get("institutionCode");
            if (institutionCode == null || institutionCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Institution Code가 필요합니다.");
            }

            adminService.updateInstitutionCode(institutionCode);
            return ResponseEntity.ok("Institution Code가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Institution Code 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Seed Key 업데이트
     */
    @PostMapping("/settings/seed-key")
    public ResponseEntity<String> updateSeedKey(@RequestBody Map<String, String> request) {
        try {
            String seedKey = request.get("seedKey");
            if (seedKey == null || seedKey.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Seed Key가 필요합니다.");
            }

            adminService.updateSeedKey(seedKey);
            return ResponseEntity.ok("Seed Key가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Seed Key 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/test-patient")
    public ResponseEntity<?> testPatient(@RequestBody Map<String, String> request) {
        try {
            String residentNumber = request.get("residentNumber");
            String userId = request.get("userId");

            if (residentNumber == null || residentNumber.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "주민번호가 비어있습니다.");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> result = adminService.testPatientRequest(residentNumber, userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "테스트 요청에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
