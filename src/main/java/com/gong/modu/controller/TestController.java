package com.gong.modu.controller;

import com.gong.modu.domain.entity.Test;
import com.gong.modu.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestRepository testRepository;

    @PostMapping("/test")
    public String test() {
        Test t = new Test();
        t.setName("공모두 테스트");
        testRepository.save(t);
        return "ok";
    }
}
