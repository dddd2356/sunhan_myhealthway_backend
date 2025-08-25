package com.example.HospitalSystemBackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "myhealthway")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyHealthWay {
    @Id
    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = true, length = 1000)
    private String seedKey;

    @Column(nullable = true, length = 1000)
    private String clientId;

    @Column(nullable = true, length = 1000)
    private String clientSecret;

    @Column(nullable = true, length = 100)
    private String utilizationServiceNo;

    @Column(nullable = true, length = 100)
    private String institutionCode;
}
