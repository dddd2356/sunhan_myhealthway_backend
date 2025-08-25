package com.example.HospitalSystemBackend.Service;

import com.example.HospitalSystemBackend.Dto.request.PatientInfoDto;
import com.example.HospitalSystemBackend.Dto.response.PatientInfoResponseDto;
import com.example.HospitalSystemBackend.Entity.MyHealthWay;
import com.example.HospitalSystemBackend.Entity.User;
import com.example.HospitalSystemBackend.Repository.MyHealthWayRepository;
import com.example.HospitalSystemBackend.Repository.PatientRepository;
import com.example.HospitalSystemBackend.Repository.UserRepository;
import com.example.HospitalSystemBackend.Util.MymdSeedCtrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private MymdSeedCtrUtil encryptionUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyHealthWayRepository myHealthWayRepository;

    private MyHealthWay getSettings() {
        List<MyHealthWay> settingsList = myHealthWayRepository.findAll();
        if (settingsList.isEmpty()) {
            throw new IllegalStateException("MyHealthWay 설정이 데이터베이스에 존재하지 않습니다.");
        }
        return settingsList.get(0);
    }


    /**
     * 현재 로그인한 사용자의 부서 코드를 Spring Security 컨텍스트에서 가져옵니다.
     * @return 로그인한 사용자의 부서 코드
     */
    private String getLoggedInUserDeptCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 없거나 인증되지 않은 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("사용자가 인증되지 않았습니다.");
        }

        Object principal = authentication.getPrincipal();

        // Case 1: Principal이 User 엔티티인 경우
        if (principal instanceof User) {
            String deptCode = ((User) principal).getDeptCode();
            if (deptCode == null || deptCode.trim().isEmpty()) {
                throw new IllegalStateException("사용자의 부서 코드가 설정되지 않았습니다.");
            }
            return deptCode;
        }

        // Case 2: Principal이 UserDetails인 경우 (Spring Security 기본)
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userRepository.findByUserIdAndJobTypeAndUseFlag(username)
                    .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다: " + username));

            String deptCode = user.getDeptCode();
            if (deptCode == null || deptCode.trim().isEmpty()) {
                throw new IllegalStateException("사용자의 부서 코드가 설정되지 않았습니다.");
            }
            return deptCode;
        }

        // Case 3: Principal이 String (username)인 경우
        if (principal instanceof String) {
            String username = (String) principal;
            User user = userRepository.findByUserIdAndJobTypeAndUseFlag(username)
                    .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다: " + username));

            String deptCode = user.getDeptCode();
            if (deptCode == null || deptCode.trim().isEmpty()) {
                throw new IllegalStateException("사용자의 부서 코드가 설정되지 않았습니다.");
            }
            return deptCode;
        }

        // Case 4: JWT에서 직접 추출 (JWT 사용자 정의 객체인 경우)
        // 만약 JWT에서 claims를 직접 사용한다면
        if (authentication.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) authentication.getDetails();
            String deptCode = (String) details.get("deptCode");
            if (deptCode != null && !deptCode.trim().isEmpty()) {
                return deptCode;
            }
        }

        throw new IllegalStateException("인증 컨텍스트에서 사용자 정보를 찾을 수 없습니다. Principal type: " + principal.getClass().getName());
    }

    /**
     * 오늘 날짜, 로그인한 사용자의 진료과 코드, 진료 상태에 따라 환자 목록을 조회합니다.
     * clncCnfrmFlag: "0" (미진료), "1" (보류), "2" (진료)
     */
    public List<PatientInfoResponseDto> getPatientsByClncFlag(Integer clncCnfrmFlag) {
        String deptCode = getLoggedInUserDeptCode(); // Spring Security 컨텍스트에서 부서 코드 조회

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<PatientInfoDto> patientList = Collections.emptyList();

        try {
            if (clncCnfrmFlag >= 0 && clncCnfrmFlag <= 2) {
                patientList = patientRepository.findPatientsByDateDeptAndStatus(today, deptCode, clncCnfrmFlag);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid clncCnfrmFlag: " + clncCnfrmFlag);
            return Collections.emptyList();
        }

        // DB에서 설정값 가져오기
        MyHealthWay settings = getSettings();
        String seedKey = settings.getSeedKey();

        return patientList.stream().map(dto -> {
            PatientInfoResponseDto responseDto = new PatientInfoResponseDto();
            responseDto.setPatId(dto.getPatId());
            responseDto.setPatName(dto.getPatName());
            responseDto.setAge(dto.getAge());
            responseDto.setDeptCode(dto.getDeptCode());
            responseDto.setPrsnIdPre(dto.getPrsnIdPre());
            responseDto.setJuminNum(dto.getJuminNum());
            String encryptedJuminNum = encryptionUtil.SEED_CTR_Encrypt(seedKey, dto.getJuminNum());
            responseDto.setEncryptedResidentNumber(encryptedJuminNum);
            responseDto.setClncCnfrmFlag(dto.getClncCnfrmFlag());
            return responseDto;
        }).collect(Collectors.toList());
    }
}
