package com.molla.controller.customer;

import com.molla.model.Category;
import com.molla.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class MainController {

    private final CategoryService categoryService;

    public MainController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String viewHomePage(Model model) {
        log.debug("MainController | viewHomePage | created");
        List<Category> categories = categoryService.listNoChildrenCategories();

        model.addAttribute("categories", categories);
        return "customer/index";
    }
}
