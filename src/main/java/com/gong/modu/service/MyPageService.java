package com.gong.modu.service;

import com.gong.modu.domain.dto.MyPageHomeResponse;
import com.gong.modu.domain.dto.MyPageHomeResponse.InvestmentProfileInfo;
import com.gong.modu.domain.dto.NicknameUpdateRequest;
import com.gong.modu.domain.dto.PasswordUpdateRequest;
import com.gong.modu.domain.entity.InvestmentPersonaType;
import com.gong.modu.domain.entity.User;
import com.gong.modu.domain.entity.UserInvestmentProfileSession;
import com.gong.modu.domain.enums.Provider;
import com.gong.modu.exception.CustomException;
import com.gong.modu.exception.ErrorCode;
import com.gong.modu.repository.UserInvestmentProfileSessionRepository;
import com.gong.modu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final UserInvestmentProfileSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MyPageHomeResponse getHome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserInvestmentProfileSession session = sessionRepository.findByUser(user).orElse(null);

        InvestmentProfileInfo profileInfo = null;
        if (session != null) {
            InvestmentPersonaType persona = session.getPersonaType();
            profileInfo = InvestmentProfileInfo.builder()
                    .id(persona.getId())
                    .personaCode(persona.getPersonaCode())
                    .koreanName(persona.getKoreanName())
                    .englishName(persona.getEnglishName())
                    .keywordTags(persona.getKeywordTags())
                    .axisSummary(persona.getAxisSummary())
                    .personaDescription(persona.getPersonaDescription())
                    .recommendedStrategy(persona.getRecommendedStrategy())
                    .warningMessage(persona.getWarningMessage())
                    .build();
        }

        return MyPageHomeResponse.builder()
                .nickname(user.getNickname())
                .provider(user.getProvider().name())
                .email(user.getEmail())
                .investmentProfile(profileInfo)
                .build();
    }

    @Transactional
    public void updateNickname(Long userId, NicknameUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getNickname().equals(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_UNCHANGED);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        user.updateNickname(request.getNickname());
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getProvider() != Provider.LOCAL) {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_NOT_SUPPORTED_FOR_PASSWORD_UPDATE);
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        user.updatePasswordHash(passwordEncoder.encode(request.getNewPassword()));
    }
}
