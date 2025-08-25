package com.example.HospitalSystemBackend.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PATMST")
@Data
public class Patient {
    @Id
    @Column(name = "PATID")
    private String patId;

    @Column(name = "PATNAME")
    private String patName;

    @Column(name = "CENT")
    private String cent;

    @Column(name = "PRSNIDPRE")
    private String prsnIdPre;

    @Column(name = "PRSNIDPOST")
    private String prsnIdPost;

    @Column(name = "MIGFLG")
    private String migFlg;
}