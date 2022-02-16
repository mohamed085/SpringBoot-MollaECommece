package com.molla.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AuthController {

    @GetMapping("/login")
    public String viewLoginPage() {
        log.debug("AuthController | viewLoginPage is called");

        return "login";
    }

}
