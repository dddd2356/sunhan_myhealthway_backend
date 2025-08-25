package com.example.HospitalSystemBackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "USRMST") // 테이블명도 대문자로 수정
@Data
public class User {

    @Id
    @Column(name = "USRID") // 컬럼명 대문자로 수정
    private String userId;

    @Column(name = "USRKORNAME") // 컬럼명 대문자로 수정
    private String userName;

    @Column(name = "JOBTYPE") // 컬럼명 대문자로 수정
    private String jobType;

    @Column(name = "DEPTCODE") // 컬럼명 대문자로 수정
    private String deptCode;

    @Column(name = "USEFLAG") // 컬럼명 대문자로 수정
    private String useFlag;
}
