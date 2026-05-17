package com.gong.modu.repository.ipo;

import com.gong.modu.domain.enums.ipo.DataSourceProvider;
import com.gong.modu.domain.enums.ipo.DataSourceType;
import com.gong.modu.domain.entity.ipo.IpoDataFieldSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


// 공모주 데이터 필드별 출처 조회 Repository
public interface IpoDataFieldSourceRepository extends JpaRepository<IpoDataFieldSource, Long> {

    // 특정 공모 이벤트의 모든 필드 출처 조회
    List<IpoDataFieldSource> findByIpoEventId(Long ipoEventId);

    // 특정 공모 이벤트의 특정 테이블/컬럼 출처 조회
    Optional<IpoDataFieldSource> findByIpoEventIdAndTargetTableAndTargetColumn(
            Long ipoEventId,
            String targetTable,
            String targetColumn
    );

    // 출처 생성 방식 기준 조회
    List<IpoDataFieldSource> findBySourceType(DataSourceType sourceType);

    // 외부 제공자 기준 조회
    List<IpoDataFieldSource> findBySourceProvider(DataSourceProvider sourceProvider);

    // 특정 공모 이벤트에서 특정 출처 방식으로 생성된 필드 목록 조회
    List<IpoDataFieldSource> findByIpoEventIdAndSourceType(Long ipoEventId, DataSourceType sourceType);
}
