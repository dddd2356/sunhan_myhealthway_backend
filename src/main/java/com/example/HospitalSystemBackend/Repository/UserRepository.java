package com.example.HospitalSystemBackend.Repository;

import com.example.HospitalSystemBackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.jobType = '1' AND u.useFlag = '1'")
    Optional<User> findByUserIdAndJobTypeAndUseFlag(@Param("userId") String userId);

    @Query("SELECT DISTINCT u.deptCode FROM User u WHERE u.useFlag = '1' AND u.jobType = '1' " +
            "AND u.deptCode NOT IN ('AA', 'AN1', 'AN2', 'CP', 'SD', 'LM', 'IA', 'DR', 'IOH', 'SCP') " +
            "ORDER BY u.deptCode")
    List<String> findDistinctDeptCodes();
}