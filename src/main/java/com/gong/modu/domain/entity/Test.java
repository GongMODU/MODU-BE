package com.gong.modu.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Test {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
