package com.gong.modu.repository;

import com.gong.modu.domain.entity.User;
import com.gong.modu.domain.entity.UserInvestmentProfileSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInvestmentProfileSessionRepository extends JpaRepository<UserInvestmentProfileSession, Long> {

    Optional<UserInvestmentProfileSession> findByUser(User user);
}
