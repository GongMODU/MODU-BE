package com.gong.modu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gong.modu.domain.dto.InvestmentAnalysisResponse;
import com.gong.modu.domain.dto.InvestmentAnswerRequest;
import com.gong.modu.domain.dto.InvestmentQuestionResponse;
import com.gong.modu.domain.dto.InvestmentQuestionResponse.OptionDto;
import com.gong.modu.domain.dto.InvestmentQuestionResponse.QuestionDto;
import com.gong.modu.domain.entity.InvestmentPersonaType;
import com.gong.modu.domain.entity.User;
import com.gong.modu.domain.entity.UserInvestmentProfileSession;
import com.gong.modu.domain.enums.KnowledgeLevel;
import com.gong.modu.domain.enums.RiskLevel;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import com.gong.modu.repository.InvestmentPersonaTypeRepository;
import com.gong.modu.repository.UserInvestmentProfileSessionRepository;
import com.gong.modu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvestmentProfileService {

    private final UserRepository userRepository;
    private final InvestmentPersonaTypeRepository personaTypeRepository;
    private final UserInvestmentProfileSessionRepository sessionRepository;
    private final ObjectMapper objectMapper;

    // ──────────── 질문 옵션 텍스트 ────────────
    private static final String[] Q1_OPTIONS = {
            "전혀 없다", "1~3회 해봤다", "4회 이상 해봤다"
    };
    private static final String[] Q2_OPTIONS = {
            "모두 알고 있다", "4~6개 알고 있다", "1~3개 알고 있다", "하나도 모른다"
    };
    private static final String[] Q3_OPTIONS = {
            "무슨 말인지 거의 모르겠다", "어느 정도 읽히지만 어렵다", "대부분 이해한다"
    };
    private static final String[] Q4_OPTIONS = {
            "상장 당일 수익 실현", "단기(1~3개월) 주가 상승", "장기 성장 기대", "일단 배정받는 경험"
    };
    private static final String[] Q5_OPTIONS = {
            "50만 원 미만", "50~200만 원", "200~500만 원", "500만 원 이상"
    };
    private static final String[] Q6_OPTIONS = {
            "즉시 손절한다", "조금 기다려본다", "물타기(추가 매수)를 고려한다", "장기 보유한다"
    };
    private static final String[] Q7_OPTIONS = {
            "10% 미만", "10~30%", "30~50%", "절반 이상"
    };
    private static final String[] Q8_OPTIONS = {
            "절대 안 한다", "다른 정보를 더 찾아보고 결정한다", "신호등보다 내 판단을 믿는다"
    };
    private static final String[] Q9_OPTIONS = {
            "예/적금만 한다", "주식 일부 + 예적금 병행", "주식/펀드 위주로 운용",
            "코인/레버리지 등 고위험 자산 포함"
    };
    private static final String[] Q10_OPTIONS = {
            "상장 당일 팔겠다", "1~3개월 내 팔겠다", "6개월~1년 이상 보유하겠다",
            "상황 봐서 / 정해두지 않겠다"
    };

    // ──────────── K축 스코어링 ────────────
    private static final KnowledgeLevel[] Q1_K = {
            KnowledgeLevel.K1, KnowledgeLevel.K2, KnowledgeLevel.K3
    };
    private static final KnowledgeLevel[] Q2_K = {
            KnowledgeLevel.K4, KnowledgeLevel.K3, KnowledgeLevel.K2, KnowledgeLevel.K1
    };
    private static final KnowledgeLevel[] Q3_K = {
            KnowledgeLevel.K1, KnowledgeLevel.K2, KnowledgeLevel.K3
    };
    private static final KnowledgeLevel[] Q4_K = {
            KnowledgeLevel.K2, KnowledgeLevel.K3, KnowledgeLevel.K4, KnowledgeLevel.K1
    };

    // ──────────── R축 스코어링 ────────────
    private static final RiskLevel[] Q4_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R3, RiskLevel.R1
    };
    private static final RiskLevel[] Q5_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R3, RiskLevel.R4
    };
    private static final RiskLevel[] Q6_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R3, RiskLevel.R4
    };
    private static final RiskLevel[] Q7_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R3, RiskLevel.R4
    };
    private static final RiskLevel[] Q8_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R4
    };
    private static final RiskLevel[] Q9_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R3, RiskLevel.R4
    };
    private static final RiskLevel[] Q10_R = {
            RiskLevel.R1, RiskLevel.R2, RiskLevel.R3, RiskLevel.R4
    };

    // ──────────── 질문 목록 조회 ────────────
    public InvestmentQuestionResponse getQuestions() {
        return InvestmentQuestionResponse.builder()
                .questions(List.of(
                        buildQuestion(1, "K", "지금까지 공모주 청약에 참여해본 적 있나요?", Q1_OPTIONS),
                        buildQuestion(2, "K", "아래 용어 중 뜻을 알고 있는 용어의 개수를 골라주세요."
                                + " (수요예측, 의무확약, 유통가능물량, 균등배정, 비례배정, 최소청약증거금, 기관경쟁률)", Q2_OPTIONS),
                        buildQuestion(3, "K", "공시 리포트나 투자 뉴스를 읽을 때 어떤 느낌인가요?", Q3_OPTIONS),
                        buildQuestion(4, "K+R", "공모주 투자에서 가장 기대하는 것은 무엇인가요?", Q4_OPTIONS),
                        buildQuestion(5, "R", "공모주 청약에 쓸 수 있는 여유 자금 규모는 어느 정도인가요?", Q5_OPTIONS),
                        buildQuestion(6, "R", "상장 후 주가가 공모가보다 10% 떨어졌다면 어떻게 하시겠어요?", Q6_OPTIONS),
                        buildQuestion(7, "R", "공모주 청약에 넣는 돈이 전체 투자 자산에서 차지하는 비중이 어느 정도인가요?", Q7_OPTIONS),
                        buildQuestion(8, "R", "신호등이 '위험'인 공모주라도 관심이 가는 기업이라면 청약할 의향이 있나요?", Q8_OPTIONS),
                        buildQuestion(9, "R", "평소 투자 자산을 어떻게 운용하고 있나요?", Q9_OPTIONS),
                        buildQuestion(10, "R", "공모주에서 기대하는 수익실현시점은 언제인가요?", Q10_OPTIONS)
                ))
                .build();
    }

    private QuestionDto buildQuestion(int number, String axis, String content, String[] options) {
        List<OptionDto> optionList = new java.util.ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            optionList.add(OptionDto.builder().index(i).content(options[i]).build());
        }
        return QuestionDto.builder()
                .questionNumber(number)
                .axis(axis)
                .content(content)
                .options(optionList)
                .build();
    }

    // ──────────── 답변 제출 및 분석 ────────────
    @Transactional
    public InvestmentAnalysisResponse analyze(Long userId, InvestmentAnswerRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        KnowledgeLevel kLevel = calculateKnowledgeLevel(request);
        RiskLevel rLevel = calculateRiskLevel(request);

        String personaCode = kLevel.name() + rLevel.name();
        InvestmentPersonaType persona = personaTypeRepository.findByPersonaCode(personaCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PERSONA_TYPE_NOT_FOUND));

        Map<String, Integer> kScoreMap = buildKnowledgeScoreMap(request);
        Map<String, Integer> rScoreMap = buildRiskScoreMap(request);
        String resultSummary = buildResultSummary(kLevel, rLevel, persona.getKoreanName());

        upsertSession(user, persona, request, kLevel, rLevel, kScoreMap, rScoreMap, resultSummary);

        return InvestmentAnalysisResponse.builder()
                .personaCode(personaCode)
                .knowledgeLevel(kLevel.name())
                .riskLevel(rLevel.name())
                .koreanName(persona.getKoreanName())
                .englishName(persona.getEnglishName())
                .keywordTags(persona.getKeywordTags())
                .axisSummary(persona.getAxisSummary())
                .personaDescription(persona.getPersonaDescription())
                .recommendedStrategy(persona.getRecommendedStrategy())
                .warningMessage(persona.getWarningMessage())
                .knowledgeScoreMap(kScoreMap)
                .riskScoreMap(rScoreMap)
                .build();
    }

    // ──────────── K축 계산 ────────────
    private KnowledgeLevel calculateKnowledgeLevel(InvestmentAnswerRequest req) {
        EnumMap<KnowledgeLevel, Integer> scores = initKScores();
        scores.merge(Q1_K[req.getQ1()], 1, Integer::sum);
        scores.merge(Q2_K[req.getQ2()], 1, Integer::sum);
        scores.merge(Q3_K[req.getQ3()], 1, Integer::sum);
        scores.merge(Q4_K[req.getQ4()], 1, Integer::sum);
        return findKMode(scores);
    }

    // ──────────── R축 계산 ────────────
    private RiskLevel calculateRiskLevel(InvestmentAnswerRequest req) {
        EnumMap<RiskLevel, Integer> scores = initRScores();
        scores.merge(Q4_R[req.getQ4()],  1, Integer::sum);
        scores.merge(Q5_R[req.getQ5()],  1, Integer::sum);
        scores.merge(Q6_R[req.getQ6()],  1, Integer::sum);
        scores.merge(Q7_R[req.getQ7()],  1, Integer::sum);
        scores.merge(Q8_R[req.getQ8()],  1, Integer::sum);
        scores.merge(Q9_R[req.getQ9()],  1, Integer::sum);
        scores.merge(Q10_R[req.getQ10()], 1, Integer::sum);
        return findRMode(scores);
    }

    // 동점 시 낮은 등급 우선 (K1 < K2 < K3 < K4 순 순회, 엄격한 초과일 때만 갱신)
    private KnowledgeLevel findKMode(EnumMap<KnowledgeLevel, Integer> scores) {
        KnowledgeLevel result = KnowledgeLevel.K1;
        for (KnowledgeLevel level : KnowledgeLevel.values()) {
            if (scores.get(level) > scores.get(result)) {
                result = level;
            }
        }
        return result;
    }

    private RiskLevel findRMode(EnumMap<RiskLevel, Integer> scores) {
        RiskLevel result = RiskLevel.R1;
        for (RiskLevel level : RiskLevel.values()) {
            if (scores.get(level) > scores.get(result)) {
                result = level;
            }
        }
        return result;
    }

    // ──────────── 스코어맵 생성 ────────────
    private Map<String, Integer> buildKnowledgeScoreMap(InvestmentAnswerRequest req) {
        EnumMap<KnowledgeLevel, Integer> scores = initKScores();
        scores.merge(Q1_K[req.getQ1()], 1, Integer::sum);
        scores.merge(Q2_K[req.getQ2()], 1, Integer::sum);
        scores.merge(Q3_K[req.getQ3()], 1, Integer::sum);
        scores.merge(Q4_K[req.getQ4()], 1, Integer::sum);

        Map<String, Integer> result = new LinkedHashMap<>();
        for (KnowledgeLevel level : KnowledgeLevel.values()) {
            result.put(level.name(), scores.get(level));
        }
        return result;
    }

    private Map<String, Integer> buildRiskScoreMap(InvestmentAnswerRequest req) {
        EnumMap<RiskLevel, Integer> scores = initRScores();
        scores.merge(Q4_R[req.getQ4()],  1, Integer::sum);
        scores.merge(Q5_R[req.getQ5()],  1, Integer::sum);
        scores.merge(Q6_R[req.getQ6()],  1, Integer::sum);
        scores.merge(Q7_R[req.getQ7()],  1, Integer::sum);
        scores.merge(Q8_R[req.getQ8()],  1, Integer::sum);
        scores.merge(Q9_R[req.getQ9()],  1, Integer::sum);
        scores.merge(Q10_R[req.getQ10()], 1, Integer::sum);

        Map<String, Integer> result = new LinkedHashMap<>();
        for (RiskLevel level : RiskLevel.values()) {
            result.put(level.name(), scores.get(level));
        }
        return result;
    }

    // ──────────── UPSERT ────────────
    private void upsertSession(
            User user,
            InvestmentPersonaType persona,
            InvestmentAnswerRequest req,
            KnowledgeLevel kLevel,
            RiskLevel rLevel,
            Map<String, Integer> kScoreMap,
            Map<String, Integer> rScoreMap,
            String resultSummary
    ) {
        String kScoreJson = toJson(kScoreMap);
        String rScoreJson = toJson(rScoreMap);

        sessionRepository.findByUser(user).ifPresentOrElse(
                existing -> existing.update(
                        persona,
                        Q4_OPTIONS[req.getQ4()],
                        Q1_OPTIONS[req.getQ1()],
                        Q2_OPTIONS[req.getQ2()],
                        Q3_OPTIONS[req.getQ3()],
                        Q5_OPTIONS[req.getQ5()],
                        Q6_OPTIONS[req.getQ6()],
                        Q7_OPTIONS[req.getQ7()],
                        Q8_OPTIONS[req.getQ8()],
                        Q9_OPTIONS[req.getQ9()],
                        Q10_OPTIONS[req.getQ10()],
                        kLevel.name(),
                        rLevel.name(),
                        kScoreJson,
                        rScoreJson,
                        resultSummary,
                        persona.getRecommendedStrategy()
                ),
                () -> sessionRepository.save(
                        UserInvestmentProfileSession.builder()
                                .user(user)
                                .personaType(persona)
                                .q1InvestmentGoal(Q4_OPTIONS[req.getQ4()])
                                .q2ExperienceCount(Q1_OPTIONS[req.getQ1()])
                                .q3TermKnowledge(Q2_OPTIONS[req.getQ2()])
                                .q4ReadingAbility(Q3_OPTIONS[req.getQ3()])
                                .q5AvailableFunds(Q5_OPTIONS[req.getQ5()])
                                .q6LossBehavior(Q6_OPTIONS[req.getQ6()])
                                .q7IpoAssetRatio(Q7_OPTIONS[req.getQ7()])
                                .q8RedSignalWillingness(Q8_OPTIONS[req.getQ8()])
                                .q9AssetManagement(Q9_OPTIONS[req.getQ9()])
                                .q10ProfitTiming(Q10_OPTIONS[req.getQ10()])
                                .knowledgeAxisResult(kLevel.name())
                                .riskAxisResult(rLevel.name())
                                .knowledgeScoreMap(kScoreJson)
                                .riskScoreMap(rScoreJson)
                                .resultSummary(resultSummary)
                                .recommendedStrategy(persona.getRecommendedStrategy())
                                .analyzedAt(LocalDateTime.now())
                                .build()
                )
        );
    }

    // ──────────── 유틸 ────────────
    private EnumMap<KnowledgeLevel, Integer> initKScores() {
        EnumMap<KnowledgeLevel, Integer> scores = new EnumMap<>(KnowledgeLevel.class);
        for (KnowledgeLevel level : KnowledgeLevel.values()) scores.put(level, 0);
        return scores;
    }

    private EnumMap<RiskLevel, Integer> initRScores() {
        EnumMap<RiskLevel, Integer> scores = new EnumMap<>(RiskLevel.class);
        for (RiskLevel level : RiskLevel.values()) scores.put(level, 0);
        return scores;
    }

    private String buildResultSummary(KnowledgeLevel kLevel, RiskLevel rLevel, String koreanName) {
        return String.format("지식축 %s, 리스크축 %s 유형으로 분석되었습니다. 투자 유형은 '%s'입니다.",
                kLevel.name(), rLevel.name(), koreanName);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
