package com.example.HospitalSystemBackend.Controller;

import com.example.HospitalSystemBackend.Dto.response.PatientInfoResponseDto;
import com.example.HospitalSystemBackend.Service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // clncCnfrmFlag를 파라미터로 받아 동적으로 조회하는 새로운 엔드포인트
    // 로그인한 사용자의 부서 코드를 사용하도록 수정
    @GetMapping
    public ResponseEntity<List<PatientInfoResponseDto>> getPatientsByClncFlag(
            @RequestParam("clncCnfrmFlag") Integer clncCnfrmFlag) {

        // userId를 따로 가져올 필요 없이, 서비스 레이어에서 직접 처리
        return ResponseEntity.ok(patientService.getPatientsByClncFlag(clncCnfrmFlag));
    }
}