package com.example.HospitalSystemBackend.Service;

import com.example.HospitalSystemBackend.Entity.MyHealthWay;
import com.example.HospitalSystemBackend.Repository.MyHealthWayRepository;
import com.example.HospitalSystemBackend.Util.MymdSeedCtrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 외부 연동을 위한 인증 요청 데이터 생성 서비스
 */
@Service
@Slf4j
public class AuthenticationService {  //우리 시스템이 외부 시스템으로 보내는 "출력"**을 준비하는 역할

    @Autowired
    private MymdSeedCtrUtil encryptionUtil;

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

    /**
     * 인증 API 호출을 위한 요청 Map 생성
     *
     * @param userId                    요청 사용자ID
     * @param residentNo                평문 주민등록번호 (SEED CTR util 로 암호화 처리)
     * @param thirdPartyInstitutionType
     * @param devMode
     * @return body 로 사용할 Map
     */
    public Map<String,Object> prepareAuthRequest(String userId, String residentNo, String thirdPartyInstitutionType, String devMode) {

        // DB에서 설정값 가져오기
        MyHealthWay settings = getSettings();

        // LinkedHashMap 으로 선언해야 JSON 순서 유지
        Map<String,Object> authRequest = new LinkedHashMap<>();

        // 1) authorization
        String rawCred = settings.getClientId() + ":" + settings.getClientSecret();
        String encodedCred = Base64.encodeBase64String(rawCred.getBytes(StandardCharsets.UTF_8));
        authRequest.put("authorization", encodedCred);
        log.info("Raw credentials: {}, Encoded: {}", rawCred, encodedCred);
        // 2) thirdPartyUtilizationServiceNo
        authRequest.put("thirdPartyUtilizationServiceNo", settings.getUtilizationServiceNo());

        // 3) thirdPartyInstitutionCode
        authRequest.put("thirdPartyInstitutionCode", settings.getInstitutionCode());
        // 주민번호 암호화 전 로그 추가
        log.info("=== 암호화 전 주민번호 상태 확인 ===");
        log.info("입력받은 주민번호: {}", residentNo);
        log.info("주민번호 길이: {}", residentNo.length());
        log.info("주민번호가 숫자만 포함하는지: {}", residentNo.matches("\\d+"));
        // 4) residentRegistrationNumber (SEED CTR 암호화)
        String encryptedRR = MymdSeedCtrUtil.SEED_CTR_Encrypt(settings.getSeedKey(), residentNo);
        authRequest.put("residentRegistrationNumber", encryptedRR);

        // 5) thirdPartyUserId
        authRequest.put("thirdPartyUserId", userId);

        // 6) thirdPartyInstitutionType
        authRequest.put("thirdPartyInstitutionType", "20");

        // 7) devMode (0: 운영, 1: 개발)
        authRequest.put("devMode", "0");
        log.info("Encrypting resident number: {}", residentNo);

        log.info("Encrypted resident number: {}", encryptedRR);
        log.info("=== 최종 전송 데이터 ===");
        log.info("residentRegistrationNumber: {}", authRequest.get("residentRegistrationNumber"));

        return authRequest;
    }
}