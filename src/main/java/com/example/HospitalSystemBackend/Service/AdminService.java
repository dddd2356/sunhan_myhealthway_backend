package com.example.HospitalSystemBackend.Service;

import com.example.HospitalSystemBackend.Dto.response.LoginResponseDto;
import com.example.HospitalSystemBackend.Entity.MyHealthWay;
import com.example.HospitalSystemBackend.Provider.JwtProvider;
import com.example.HospitalSystemBackend.Repository.MyHealthWayRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private JwtProvider jwtProvider;

    @Value("${ADMIN_USER_ID}")
    private String ADMIN_USER_ID;
    // 관리자 비밀번호 (실제 환경에서는 암호화해서 저장해야 함)
    @Value("${ADMIN_PASSWORD}")
    private String ADMIN_PASSWORD;

    @Autowired
    private MyHealthWayRepository myHealthWayRepository;

    /**
     * DB에서 설정값들을 가져옵니다.
     */
    private MyHealthWay getSettings() {
        List<MyHealthWay> settingsList = myHealthWayRepository.findAll();
        if (settingsList.isEmpty()) {
            throw new IllegalStateException("MyHealthWay 설정이 데이터베이스에 존재하지 않습니다.");
        }
        return settingsList.get(0);
    }

    public boolean validateAdminCredentials(String userId, String password) {
        return ADMIN_USER_ID.equals(userId) && ADMIN_PASSWORD.equals(password);
    }

    public boolean isAdminUser(String userId) {
        return ADMIN_USER_ID.equals(userId);
    }

    /**
     * 관리자 로그인 처리 - 실제 JWT 토큰 생성
     */
    public LoginResponseDto adminLogin(String userId, String password) {
        if (validateAdminCredentials(userId, password)) {
            // 실제 JWT 토큰 생성
            String token = jwtProvider.generateToken(userId);

            // 관리자용 LoginResponseDto 생성 (새로 추가된 생성자 사용)
            return new LoginResponseDto(token, userId, true);
        }
        throw new RuntimeException("관리자 인증에 실패했습니다");
    }

    public Map<String, Object> getAdminSettings() {
        MyHealthWay settings = getSettings();

        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("thirdPartyAuthUrl", settings.getUrl());
        settingsMap.put("clientId", settings.getClientId());
        settingsMap.put("clientSecret", settings.getClientSecret());
        settingsMap.put("utilizationServiceNo", settings.getUtilizationServiceNo());
        settingsMap.put("institutionCode", settings.getInstitutionCode());
        settingsMap.put("seedKey", settings.getSeedKey());

        return settingsMap;
    }

    /**
     * WebViewerService에서 필요로 하는, 현재 DB에 저장된 URL을 반환합니다.
     */
    @Transactional(readOnly = true)
    public String getCurrentThirdPartyAuthUrl() {
        return myHealthWayRepository.findAll().stream()
                .map(MyHealthWay::getUrl)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("MyHealthWay 설정이 없습니다."));
    }

    /**
     * URL 업데이트 (native query 사용)
     */
    @Transactional
    public void updateThirdPartyAuthUrl(String newUrl) {
        MyHealthWay settings = getSettings();
        String oldUrl = settings.getUrl();
        int updated = myHealthWayRepository.updateUrl(oldUrl, newUrl);
        if (updated == 0) {
            throw new IllegalStateException("URL 변경에 실패했습니다: " + oldUrl);
        }
    }

    /**
     * Client ID 업데이트
     */
    public void updateClientId(String clientId) {
        MyHealthWay settings = getSettings();
        settings.setClientId(clientId);
        myHealthWayRepository.save(settings);
    }

    /**
     * Client Secret 업데이트
     */
    public void updateClientSecret(String clientSecret) {
        MyHealthWay settings = getSettings();
        settings.setClientSecret(clientSecret);
        myHealthWayRepository.save(settings);
    }

    /**
     * Utilization Service No 업데이트
     */
    public void updateUtilizationServiceNo(String utilizationServiceNo) {
        MyHealthWay settings = getSettings();
        settings.setUtilizationServiceNo(utilizationServiceNo);
        myHealthWayRepository.save(settings);
    }

    /**
     * Institution Code 업데이트
     */
    public void updateInstitutionCode(String institutionCode) {
        MyHealthWay settings = getSettings();
        settings.setInstitutionCode(institutionCode);
        myHealthWayRepository.save(settings);
    }

    /**
     * Seed Key 업데이트
     */
    public void updateSeedKey(String seedKey) {
        MyHealthWay settings = getSettings();
        settings.setSeedKey(seedKey);
        myHealthWayRepository.save(settings);
    }

    public Map<String, Object> testPatientRequest(String residentNumber, String userId) {
        String url = getCurrentThirdPartyAuthUrl();

        Map<String, Object> testResult = new HashMap<>();
        testResult.put("success", true);
        testResult.put("message", "테스트 요청이 성공적으로 처리되었습니다.");
        testResult.put("residentNumber", residentNumber);
        testResult.put("userId", userId);
        testResult.put("currentUrl", url);
        return testResult;
    }
}