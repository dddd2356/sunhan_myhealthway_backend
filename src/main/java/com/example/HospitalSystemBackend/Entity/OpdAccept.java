package com.example.HospitalSystemBackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "OPDACPT")
@Data
public class OpdAccept {
    @Id
    @Column(name = "PATID")
    private String patId;

    @Column(name = "OPDDATE")
    private String opdDate;

    @Column(name = "DEPTCODE")
    private String deptCode;

    @Column(name = "CLNCCNFRMFLAG")
    private Integer clncCnfrmFlag;

    @Column(name = "ACPTCANCELFLAG")
    private Integer acptCancelFlag;
}