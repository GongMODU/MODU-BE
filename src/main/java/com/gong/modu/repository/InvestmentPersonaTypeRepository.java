package com.gong.modu.repository;

import com.gong.modu.domain.entity.InvestmentPersonaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvestmentPersonaTypeRepository extends JpaRepository<InvestmentPersonaType, Long> {

    Optional<InvestmentPersonaType> findByPersonaCode(String personaCode);

    boolean existsByPersonaCode(String personaCode);
}
