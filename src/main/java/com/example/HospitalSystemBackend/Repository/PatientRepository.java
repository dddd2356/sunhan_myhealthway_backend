package com.example.HospitalSystemBackend.Repository;

import com.example.HospitalSystemBackend.Dto.request.PatientInfoDto;
import com.example.HospitalSystemBackend.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    @Query(value =
            "SELECT " +
                    "  o.patid                        AS patId, " +
                    "  p.patname                      AS patName, " +
                    "  (SELECT TRUNC(MONTHS_BETWEEN(TRUNC(SYSDATE), TO_DATE(CONCAT(p.cent, p.prsnidpre),'YYYYMMDD'))/12) FROM DUAL) AS age, " +
                    "  o.deptcode                     AS deptCode, " +
                    "  p.prsnidpre                    AS prsnIdPre, " +
                    "  CONCAT(p.prsnidpre, DECODE(p.migflg, '1', FN_EM_DECRYPT_PRSNINFO(o.patid), p.prsnidpost)) AS juminNum, " +
                    "  o.clnccnfrmflag                AS clncCnfrmFlag " +
                    "FROM opdacpt o " +
                    "JOIN patmst p ON o.patid = p.patid " +
                    "WHERE o.opddate        = :opdDate " +
                    "  AND o.deptcode       = :deptCode " +
                    "  AND o.clnccnfrmflag  = :clncCnfrmFlag " +
                    "  AND o.acptcancelflag = '0'",
            nativeQuery = true)
    List<PatientInfoDto> findPatientsByDateDeptAndStatus(
            @Param("opdDate")  String opdDate,
            @Param("deptCode") String deptCode,
            @Param("clncCnfrmFlag") Integer clncCnfrmFlag
    );
}