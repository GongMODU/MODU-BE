package com.gong.modu.security;

import com.gong.modu.domain.entity.User;
import com.gong.modu.domain.enums.Provider;
import com.gong.modu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerUserId;
        String email;
        String nickname;

        if (provider == Provider.GOOGLE) {
            providerUserId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
        } else if (provider == Provider.KAKAO) {
            providerUserId = String.valueOf(attributes.get("id"));
            @SuppressWarnings("unchecked")
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        final String finalProviderUserId = providerUserId;
        final String finalEmail = email != null
                ? email
                : provider.name().toLowerCase() + "_" + providerUserId;
        final String finalNickname = (nickname == null || userRepository.existsByNickname(nickname))
                ? "user_" + UUID.randomUUID().toString().substring(0, 8)
                : nickname;
        final Provider finalProvider = provider;

        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> userRepository.findByEmail(finalEmail)
                        .orElseGet(() -> userRepository.save(
                                User.builder()
                                        .email(finalEmail)
                                        .nickname(finalNickname)
                                        .provider(finalProvider)
                                        .providerUserId(finalProviderUserId)
                                        .build()
                        )));

        return new CustomOAuth2User(user, attributes);
    }
}
