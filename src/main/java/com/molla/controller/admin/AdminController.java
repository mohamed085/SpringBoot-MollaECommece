package com.molla.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("")
    public String getHomePage() {
        log.debug("AdminController | GetHomePage");

        return "admin/index";
    }

}
