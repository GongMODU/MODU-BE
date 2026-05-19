package com.gong.modu.repository;

import com.gong.modu.domain.entity.ApiUsageSummary;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ApiUsageSummaryRepository extends JpaRepository<ApiUsageSummary, Long> {

    // 사용량 갱신 시 동시 호출로 인한 수치 오염을 막기 위해 비관적 잠금 사용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM ApiUsageSummary u WHERE u.id = 1")
    Optional<ApiUsageSummary> findForUpdate();
}
