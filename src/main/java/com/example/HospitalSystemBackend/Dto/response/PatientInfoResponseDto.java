package com.example.HospitalSystemBackend.Dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PatientInfoResponseDto {
    private String patId;
    private String patName;
    private BigDecimal age;
    private String deptCode;
    private String prsnIdPre;
    private String juminNum;
    private String encryptedResidentNumber;
    private Integer clncCnfrmFlag;
}