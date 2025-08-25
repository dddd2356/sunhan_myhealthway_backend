package com.example.HospitalSystemBackend.Repository;

import com.example.HospitalSystemBackend.Entity.MyHealthWay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MyHealthWayRepository extends JpaRepository<MyHealthWay, String> {
    // String 은 현재 @Id가 url 이기 때문입니다.
    /**
     * Native query to update the primary key (url) of MyHealthWay without adding extra columns.
     * @param oldUrl 기존 url 값
     * @param newUrl 변경할 새 url 값
     * @return 수정된 행(row) 수
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE myhealthway SET url = :newUrl WHERE url = :oldUrl", nativeQuery = true)
    int updateUrl(@Param("oldUrl") String oldUrl,
                  @Param("newUrl") String newUrl);
}
