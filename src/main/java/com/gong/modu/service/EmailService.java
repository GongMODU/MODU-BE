package com.gong.modu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[MODU] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n\n유효 시간은 5분입니다.");
        mailSender.send(message);
        log.info("인증 코드 이메일 전송 완료: {}", to);
    }

    public void sendPasswordResetLink(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[MODU] 비밀번호 재설정");
        message.setText("비밀번호 재설정 링크: " + resetLink + "\n\n유효 시간은 30분입니다.");
        mailSender.send(message);
        log.info("비밀번호 재설정 이메일 전송 완료: {}", to);
    }
}
