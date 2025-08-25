package com.example.HospitalSystemBackend.Dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class PatientInfoDto {
    private String patId;
    private String patName;
    private BigDecimal age;
    private String deptCode;
    private String prsnIdPre;
    private String juminNum;
    private Integer  clncCnfrmFlag;

    // **이 생성자를 반드시 추가해야 합니다.**
    public PatientInfoDto(String patId, String patName, BigDecimal age, String deptCode, String prsnIdPre, String juminNum, Integer  clncCnfrmFlag) {
        this.patId = patId;
        this.patName = patName;
        this.age = age;
        this.deptCode = deptCode;
        this.prsnIdPre = prsnIdPre;
        this.juminNum = juminNum;
        this.clncCnfrmFlag = clncCnfrmFlag;
    }
}