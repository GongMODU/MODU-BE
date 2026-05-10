package com.gong.modu.domain.entity.ipo;

import com.gong.modu.domain.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// 증권사 마스터 정보를 저장하는 엔티티
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "brokers")
public class Broker extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name; // 증권사명

    @Size(max = 50)
    @Column(name = "short_name", length = 50)
    private String shortName; // 축약명

    // 증권사명
    public void updateName(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }
}
