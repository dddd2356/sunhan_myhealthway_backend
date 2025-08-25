package com.example.HospitalSystemBackend.Dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WebViewerRequestDto {  //외부에서 우리 시스템으로 들어오는 요청의 "입력" 역할을 합니다.
    private String thirdPartyUserId; //외부 시스템에서 사용하는 사용자의 고유 식별자입니다.
    private String patientId;
    private String deptCode;
    private String residentNumber;
    private String thirdPartyInstitutionType;
    private String devMode;
}