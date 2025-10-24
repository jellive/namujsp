package com.jell.namujsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 */
@Controller
public class HomeController {

    /**
     * 메인 페이지 - 메인페이지 문서로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/wiki/view/메인페이지";
    }
}
