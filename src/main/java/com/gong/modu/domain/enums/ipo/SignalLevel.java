package com.gong.modu.domain.enums.ipo;

// 공모주 위험 신호등 도메인 enum
// ipo_metrics.signal_level 컬럼에 들어갈 신호등 등급
public enum SignalLevel {
    GREEN, // 상대적으로 위험도가 낮은 상태
    YELLOW, // 주의가 필요한 중간 위험 상태
    RED // 위험도가 높은 상태
}
