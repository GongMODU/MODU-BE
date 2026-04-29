package com.gong.modu.init;

import com.gong.modu.domain.entity.InvestmentPersonaType;
import com.gong.modu.repository.InvestmentPersonaTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonaDataInitializer implements ApplicationRunner {

    private final InvestmentPersonaTypeRepository personaTypeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (personaTypeRepository.count() > 0) {
            return;
        }

        log.info("투자 성향 페르소나 시드 데이터 삽입 시작");
        personaTypeRepository.saveAll(buildPersonaList());
        log.info("투자 성향 페르소나 시드 데이터 삽입 완료 (16개)");
    }

    private List<InvestmentPersonaType> buildPersonaList() {
        return List.of(
                InvestmentPersonaType.builder()
                        .personaCode("K1R1")
                        .knowledgeAxis("K1")
                        .riskAxis("R1")
                        .koreanName("안심 탐험가")
                        .englishName("Safety Explorer")
                        .keywordTags("[\"첫 청약\", \"소액\", \"신호등 의존\"]")
                        .axisSummary("공모주가 처음이고 손실에 민감한 입문 단계")
                        .personaDescription("공모주 자체가 생소하고 용어도 낯설지만 한번 경험해보고 싶은 단계입니다. 손실에 매우 민감하며 소액으로 안전하게 시작하려는 성향입니다. 신호등 같은 명확한 지표에 크게 의존합니다.")
                        .recommendedStrategy("신호등이 초록인 공모주 중 균등배정 위주로 소액 청약을 경험해보세요. 당장 수익보다 프로세스를 익히는 것이 목표입니다.")
                        .warningMessage("수익보다 경험이 먼저입니다. 결과에 상관없이 청약 과정을 배우는 데 집중하세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K1R2")
                        .knowledgeAxis("K1")
                        .riskAxis("R2")
                        .koreanName("조심스런 입문자")
                        .englishName("Careful Newcomer")
                        .keywordTags("[\"추천 선호\", \"경험 없음\", \"쉬운 설명\"]")
                        .axisSummary("공모주가 처음이지만 적정 수익을 기대하며 신중하게 접근하는 단계")
                        .personaDescription("공모주 청약은 처음이지만 단순 경험 이상의 수익을 기대합니다. 스스로 판단하기 어려워 추천이나 큐레이션에 의존하는 경향이 있습니다.")
                        .recommendedStrategy("신호등 초록 + 기관경쟁률 높은 종목 위주로 시작하고, 추천 해설을 꼼꼼히 읽으며 판단 근거를 쌓아가세요.")
                        .warningMessage("남의 추천만 따르다 보면 왜 그 결정을 했는지 모르게 됩니다. 추천 이유를 반드시 확인하는 습관을 들이세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K1R3")
                        .knowledgeAxis("K1")
                        .riskAxis("R3")
                        .koreanName("과감한 첫걸음")
                        .englishName("Bold Beginner")
                        .keywordTags("[\"의욕 과잉\", \"리스크 맹점\", \"교육 필요\"]")
                        .axisSummary("공모주 경험은 없지만 높은 수익을 위해 과감하게 도전하려는 단계")
                        .personaDescription("공모주가 처음임에도 불구하고 높은 수익을 기대하며 위험을 감수하려는 성향입니다. 의욕은 높지만 지식 부족으로 리스크를 제대로 인지하지 못할 수 있습니다.")
                        .recommendedStrategy("먼저 유통가능물량, 의무확약 등 핵심 지표를 학습한 뒤 청약에 임하세요. 첫 청약은 소액으로 시작해 리스크를 직접 체감하는 것이 중요합니다.")
                        .warningMessage("공모주는 상장 당일 무조건 오르지 않습니다. 첫 청약부터 큰 금액을 넣는 것은 위험합니다.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K1R4")
                        .knowledgeAxis("K1")
                        .riskAxis("R4")
                        .koreanName("무경험 도박사")
                        .englishName("Uninformed Gambler")
                        .keywordTags("[\"고위험\", \"경고 필요\", \"단계적 안내\"]")
                        .axisSummary("공모주 지식 없이 고위험 투자를 시도하는 가장 주의가 필요한 단계")
                        .personaDescription("공모주에 대한 이해 없이 자산의 많은 부분을 투입하거나 신호등과 무관하게 투자하려는 성향입니다. 손실 가능성이 가장 높은 유형입니다.")
                        .recommendedStrategy("지금 당장 큰 금액 투자보다 기본 용어 학습과 소액 경험이 먼저입니다. 공모주 구조를 이해한 뒤 점진적으로 금액을 늘려가세요.")
                        .warningMessage("지식 없는 고위험 투자는 손실로 이어질 가능성이 높습니다. 반드시 기초를 먼저 익히세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K2R1")
                        .knowledgeAxis("K2")
                        .riskAxis("R1")
                        .koreanName("공부하는 관망자")
                        .englishName("Learning Observer")
                        .keywordTags("[\"콘텐츠 선호\", \"신중\", \"이해 후 참여\"]")
                        .axisSummary("공모주를 배우고 있지만 손실 회피 성향으로 직접 참여에 신중한 단계")
                        .personaDescription("공모주에 관심을 갖고 공부 중이지만 아직 직접 참여는 부담스럽습니다. 이해가 된 후에 소액으로 참여하려는 신중한 성향입니다.")
                        .recommendedStrategy("관심 있는 공모주의 수요예측 결과와 신호등을 꾸준히 추적하며 흐름을 익히세요. 이해가 충분히 됐다 싶을 때 소액으로 첫 청약을 해보세요.")
                        .warningMessage("너무 오래 관망만 하면 실전 감각을 기르기 어렵습니다. 소액으로라도 직접 경험해보세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K2R2")
                        .knowledgeAxis("K2")
                        .riskAxis("R2")
                        .koreanName("체계적 학습자")
                        .englishName("Systematic Learner")
                        .keywordTags("[\"성장 잠재력\", \"이해 기반\", \"균형적\"]")
                        .axisSummary("배우면서 균형 잡힌 투자를 실천하는 가장 이상적인 성장 단계")
                        .personaDescription("공모주 관련 지식을 쌓아가며 균형 잡힌 시각으로 투자에 접근합니다. 이해를 바탕으로 적절한 수익과 위험의 균형을 맞추려 합니다.")
                        .recommendedStrategy("기관경쟁률과 유통가능물량을 함께 확인하는 습관을 들이세요. 신호등을 참고하되 그 근거도 함께 읽으면 빠르게 실전형으로 성장할 수 있습니다.")
                        .warningMessage("지식이 쌓일수록 과신이 생길 수 있습니다. 항상 근거를 확인하는 습관을 유지하세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K2R3")
                        .knowledgeAxis("K2")
                        .riskAxis("R3")
                        .koreanName("도전적 자기학습자")
                        .englishName("Ambitious Self-Learner")
                        .keywordTags("[\"학습 의지 높음\", \"판단 조급\", \"근거 필요\"]")
                        .axisSummary("배우는 단계이지만 높은 수익을 위해 과감하게 도전하는 성향")
                        .personaDescription("공모주를 배우는 중이지만 수익에 대한 의지가 강해 위험을 감수하는 성향입니다. 학습 속도보다 투자 욕구가 앞서는 경향이 있습니다.")
                        .recommendedStrategy("투자 결정 전 반드시 해당 공모주의 핵심 지표 3가지 이상을 확인하는 루틴을 만드세요. 빠른 판단보다 근거 있는 판단이 수익률을 높입니다.")
                        .warningMessage("배우는 단계에서의 고위험 투자는 운에 의존하게 됩니다. 학습과 투자 속도를 맞춰가세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K2R4")
                        .knowledgeAxis("K2")
                        .riskAxis("R4")
                        .koreanName("지식 부족 승부사")
                        .englishName("Underqualified Bettor")
                        .keywordTags("[\"과신 편향\", \"리스크 노출\", \"감속 필요\"]")
                        .axisSummary("배우는 단계임에도 자산의 큰 비중을 공모주에 투입하는 고위험 성향")
                        .personaDescription("공모주 지식이 아직 충분하지 않은 상태에서 자신의 판단을 과도하게 신뢰하며 큰 비중을 투입하려는 성향입니다.")
                        .recommendedStrategy("투자 비중을 전체 자산의 20% 이하로 제한하고, 한 종목에 집중하지 마세요. 지식과 경험이 쌓인 후 비중을 늘려가는 전략이 유리합니다.")
                        .warningMessage("지식 없는 고비중 투자는 큰 손실로 이어질 수 있습니다. 지금은 비중 조절이 최우선입니다.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K3R1")
                        .knowledgeAxis("K3")
                        .riskAxis("R1")
                        .koreanName("경험 많은 안전론자")
                        .englishName("Seasoned Conservative")
                        .keywordTags("[\"실전 경험\", \"지표 의존\", \"손실 회피\"]")
                        .axisSummary("청약 경험은 풍부하지만 안전을 최우선으로 하는 보수적 실전 투자자")
                        .personaDescription("청약 프로세스에 익숙하고 핵심 지표를 활용할 줄 알지만, 손실 회피 성향이 강해 안정적인 종목 위주로만 참여합니다. 신호등 같은 명확한 지표를 신뢰합니다.")
                        .recommendedStrategy("신호등 초록 + 기관 의무확약 비율이 높은 종목을 중심으로 선별적으로 참여하세요. 안정성을 유지하면서도 지표 활용 범위를 조금씩 넓혀보세요.")
                        .warningMessage("지나친 안전 추구는 수익 기회를 놓칠 수 있습니다. 근거가 충분한 종목은 과감히 참여해보세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K3R2")
                        .knowledgeAxis("K3")
                        .riskAxis("R2")
                        .koreanName("실전 균형투자자")
                        .englishName("Balanced Practitioner")
                        .keywordTags("[\"핵심 타깃\", \"안정적\", \"해설 활용\"]")
                        .axisSummary("경험과 균형 잡힌 판단력을 갖춘 안정적인 실전 투자자")
                        .personaDescription("청약 프로세스에 익숙하고 핵심 지표를 이해하며 안정적인 수익을 추구합니다. 해설과 데이터를 균형 있게 활용하는 성숙한 투자 스타일입니다.")
                        .recommendedStrategy("수요예측 결과와 유통가능물량을 함께 분석해 핵심 타깃 종목을 선별하세요. 지금의 균형 잡힌 접근을 유지하면서 복잡한 지표로 분석 범위를 넓혀가세요.")
                        .warningMessage("익숙한 지표에만 의존하면 새로운 유형의 공모주 기회를 놓칠 수 있습니다.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K3R3")
                        .knowledgeAxis("K3")
                        .riskAxis("R3")
                        .koreanName("분석적 도전자")
                        .englishName("Analytical Challenger")
                        .keywordTags("[\"심화 분석 선호\", \"근거 중심\", \"능동적\"]")
                        .axisSummary("경험을 바탕으로 데이터 근거가 있다면 위험을 감수하며 도전하는 투자자")
                        .personaDescription("청약 프로세스에 익숙하고 복잡한 지표도 어느 정도 이해합니다. 빨간 신호등이라도 자신만의 근거가 있다면 참여를 검토하는 능동적 투자 성향입니다.")
                        .recommendedStrategy("빨간 신호등 종목 참여 시 반드시 유통가능물량, 기관 수요예측, 재무 지표를 직접 확인하는 프로세스를 만드세요. 근거 있는 도전은 좋지만 체크리스트를 지키세요.")
                        .warningMessage("경험이 과신으로 이어지지 않도록 주의하세요. 근거 없는 직감 투자는 위험합니다.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K3R4")
                        .knowledgeAxis("K3")
                        .riskAxis("R4")
                        .koreanName("데이터 기반 승부사")
                        .englishName("Data-Driven Bettor")
                        .keywordTags("[\"고급 데이터\", \"자체 판단\", \"고활용\"]")
                        .axisSummary("풍부한 경험과 데이터 활용 능력으로 고위험 투자를 주도적으로 결정하는 투자자")
                        .personaDescription("청약 경험이 많고 데이터를 적극 활용하며 자신의 판단을 신뢰합니다. 신호등보다 직접 분석한 결과를 우선시하고 자산의 큰 비중을 공모주에 투입합니다.")
                        .recommendedStrategy("공시 원문과 수요예측 데이터를 직접 분석하는 자신만의 체계를 더욱 정교화하세요. 고비중 투자 시에는 분산 청약으로 리스크를 관리하세요.")
                        .warningMessage("데이터 분석 능력이 높아도 시장 변수는 예측 불가합니다. 비중 관리를 철저히 하세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K4R1")
                        .knowledgeAxis("K4")
                        .riskAxis("R1")
                        .koreanName("원칙주의 장인")
                        .englishName("Principled Expert")
                        .keywordTags("[\"원칙 보유\", \"낮은 비중\", \"공시 직독\"]")
                        .axisSummary("공시 원문을 읽고 자신만의 원칙으로 안전하게 운용하는 고수")
                        .personaDescription("공모주 전반을 깊이 이해하고 공시 원문도 직접 읽을 수 있습니다. 자신만의 투자 원칙이 확고하며 수익보다 원칙 준수를 우선시합니다. 낮은 비중으로 안정적으로 운용합니다.")
                        .recommendedStrategy("현재의 원칙 기반 접근을 유지하세요. 원칙에 맞는 종목만 선별해 참여하고 비중 관리로 장기적인 안정성을 확보하세요.")
                        .warningMessage("원칙에 너무 얽매이면 좋은 기회를 놓칠 수 있습니다. 가끔은 유연하게 판단해보세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K4R2")
                        .knowledgeAxis("K4")
                        .riskAxis("R2")
                        .koreanName("전략적 포트폴리오어")
                        .englishName("Strategic Allocator")
                        .keywordTags("[\"포트폴리오 사고\", \"전략적\", \"정보 능동 수집\"]")
                        .axisSummary("공모주를 포트폴리오 전략의 일부로 운용하는 전문 투자자")
                        .personaDescription("공모주를 단독 투자가 아닌 전체 포트폴리오 전략 안에서 배분합니다. 정보를 능동적으로 수집하고 균형 잡힌 수익을 추구하는 전문적인 투자 스타일입니다.")
                        .recommendedStrategy("공모주 청약을 섹터별로 분산하고, 전체 포트폴리오 내 비중을 전략적으로 조절하세요. 수요예측 데이터를 활용해 기대수익 대비 리스크를 정량적으로 평가하세요.")
                        .warningMessage("전략적 접근도 좋지만 개별 종목 리스크를 과소평가하지 마세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K4R3")
                        .knowledgeAxis("K4")
                        .riskAxis("R3")
                        .koreanName("독자적 성장투자자")
                        .englishName("Independent Growth Investor")
                        .keywordTags("[\"독자 분석\", \"섹터 중심\", \"고수익 지향\"]")
                        .axisSummary("독자적인 분석으로 높은 수익을 추구하는 고지식 성장 투자자")
                        .personaDescription("공시 원문을 직접 읽고 섹터별 성장성을 분석해 자신만의 기준으로 투자합니다. 높은 수익을 위해 상당한 위험도 감수하며 큐레이션보다 자체 분석을 신뢰합니다.")
                        .recommendedStrategy("관심 섹터의 공모주 공시를 직접 분석하고 성장성 지표를 중심으로 종목을 선별하세요. 자체 분석 결과를 기록해 투자 근거를 지속적으로 검증하세요.")
                        .warningMessage("독자적 분석에 대한 과신은 편향을 만듭니다. 반대 의견과 리스크 요인도 반드시 검토하세요.")
                        .build(),

                InvestmentPersonaType.builder()
                        .personaCode("K4R4")
                        .knowledgeAxis("K4")
                        .riskAxis("R4")
                        .koreanName("자기확신 공격투자자")
                        .englishName("Conviction-Driven Aggressor")
                        .keywordTags("[\"고집중 투자\", \"데이터 원본\", \"큐레이션 불필요\"]")
                        .axisSummary("공시 원문 수준의 이해를 바탕으로 확신에 찬 고위험 투자를 주도하는 최상위 투자자")
                        .personaDescription("공모주의 모든 프로세스와 지표를 깊이 이해하고 자신의 판단에 강한 확신을 가집니다. 신호등이나 추천 없이 공시 원문 데이터만으로 독자적인 투자 결정을 내립니다.")
                        .recommendedStrategy("자체 분석 체계를 문서화하고 투자 결과와 대조하며 지속적으로 정교화하세요. 확신이 높은 종목에 집중하되 단일 종목 과집중은 피하세요.")
                        .warningMessage("최고 수준의 지식도 시장의 불확실성을 완전히 제거하지 못합니다. 확신이 강할수록 리스크 시나리오를 더 꼼꼼히 검토하세요.")
                        .build()
        );
    }
}
