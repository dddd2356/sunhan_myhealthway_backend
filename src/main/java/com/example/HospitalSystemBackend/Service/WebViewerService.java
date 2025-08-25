package com.example.HospitalSystemBackend.Service;

import com.example.HospitalSystemBackend.Dto.request.WebViewerRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@Slf4j
public class WebViewerService {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private AdminService adminService;

    public String authenticateAndGetFinalUrl(WebViewerRequestDto request) {
        // null 값 유효성 검사
        if (request.getThirdPartyUserId() == null) {
            throw new IllegalArgumentException("ThirdPartyUserId cannot be null.");
        }
        if (request.getResidentNumber() == null) {
            throw new IllegalArgumentException("ResidentNumber cannot be null.");
        }
        log.info("Test patient request received. Resident Number: {}, User ID: {}", request.getResidentNumber(), request.getThirdPartyUserId());
        log.info("WebViewerService::authenticateAndGetFinalUrl - 요청으로 받은 주민번호: {}", request.getResidentNumber());
        try {
            // 1) 인증 요청 데이터 준비
            Map<String, Object> authRequest = authService.prepareAuthRequest(
                    request.getThirdPartyUserId(),
                    request.getResidentNumber(),
                    request.getThirdPartyInstitutionType(),
                    request.getDevMode());
            // 2) 암호화된 주민등록번호 디버깅 로그 추가
            String encryptedResidentNumber = (String) authRequest.get("residentRegistrationNumber");
            log.info("암호화된 주민등록번호 (residentRegistrationNumber): {}", encryptedResidentNumber);

            // 3) 바로 GET 요청용 최종 URL을 생성합니다.
            String finalUrl = buildFinalUrlWithParams(authRequest);

            // 4) 최종 URL 디버깅 로그 추가
            log.info("최종 웹뷰어 URL: {}", finalUrl);
            // 2) POST 요청을 보내는 대신, 바로 GET 요청용 최종 URL을 생성합니다.
            //    원래 POST 요청은 응답을 사용하지 않으므로 불필요한 지연을 유발합니다.
            return finalUrl;

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to authenticate with external service: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during authentication: " + e.getMessage(), e);
        }
    }

    /**
     * 외부 API가 요구하는 형식에 맞춰 최종 URL을 생성
     * AdminService에서 현재 설정된 URL을 가져옴
     */
    private String buildFinalUrlWithParams(Map<String, Object> authRequest) {
        // AdminService에서 현재 URL 가져오기
        String currentUrl = adminService.getCurrentThirdPartyAuthUrl();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(currentUrl);

        for (Map.Entry<String, Object> entry : authRequest.entrySet()) {
            String value = (entry.getValue() != null) ? entry.getValue().toString() : "";
            // '='와 '+' 문자를 인코딩하지 않고 그대로 사용
            builder.queryParam(entry.getKey(), value);
        }

        return builder.toUriString();
    }
}