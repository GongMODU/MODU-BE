package com.gong.modu.domain.entity.user;

import com.gong.modu.domain.entity.BaseTimeEntity;
import com.gong.modu.domain.entity.ipo.IpoEvent;
import jakarta.persistence.*;
import lombok.*;

// 사용자가 관심 공모주로 저장한 내역을 표현하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "user_interest_ipos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_interest_ipos_user_event",
                        columnNames = {"user_id", "ipo_event_id"}
                )
        }
)
public class UserInterestIpo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관심 공모주를 등록한 사용자
    // 한 사용자는 여러 공모주를 관심 등록할 수 있으므로 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 사용자가 관심 등록한 공모 이벤트
    // 하나의 공모 이벤트는 여러 사용자에게 관심 등록될 수 있으므로 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ipo_event_id", nullable = false)
    private IpoEvent ipoEvent;
}
