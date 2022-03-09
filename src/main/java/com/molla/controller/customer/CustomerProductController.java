package com.molla.controller.customer;

import com.molla.exciptions.CategoryNotFoundException;
import com.molla.exciptions.ProductNotFoundException;
import com.molla.model.Category;
import com.molla.model.Product;
import com.molla.service.CategoryService;
import com.molla.service.ProductService;
import com.molla.service.serviceImp.ProductServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class CustomerProductController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CustomerProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
                                        Model model) {

        log.debug("ProductController | viewCategoryFirstPage is called");

        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                                     @PathVariable("pageNum") int pageNum,
                                     Model model) {

        try {
            log.debug("CustomerProductController | viewCategoryByPage is called");

            Category category = categoryService.getEnabledCategoryByAlias(alias);

            log.debug("CustomerProductController | viewCategoryByPage | category : " + category.toString());


            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());

            List<Product> listProducts = pageProducts.getContent();

            long startCount = (pageNum - 1) * ProductServiceImp.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + ProductServiceImp.PRODUCTS_PER_PAGE - 1;

            log.debug("CustomerProductController | viewCategoryByPage | startCount : " + startCount);
            log.debug("CustomerProductController | viewCategoryByPage | endCount : " + endCount);

            log.debug("CustomerProductController | viewCategoryByPage | endCount > pageProducts.getTotalElements()");
            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("category", category);

            return "customer/products/product_by_category";


        } catch (CategoryNotFoundException ex) {
            return "error/404";
        }
    }


    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {

        log.debug("CustomerProductController | viewProductDetail is called");

        try {
            Product product = productService.getByAlias(alias);

            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());

            log.debug("CustomerProductController | viewProductDetail | listCategoryParents : " + listCategoryParents.toString());
            log.debug("CustomerProductController | viewProductDetail | product : " + product.toString());
            log.debug("CustomerProductController | viewCategoryByPage | pageTitle : " + product.getShortName());

            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getShortName());

            return "customer/products/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@RequestParam("keyword") String keyword, Model model) {

        log.debug("CustomerProductController | searchFirstPage is called");
        log.debug("CustomerProductController | searchFirstPage | keyword: " + keyword);

        return searchByPage(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@RequestParam("keyword") String keyword,
                               @PathVariable("pageNum") int pageNum,
                               Model model) {

        log.debug("CustomerProductController | searchByPage is called");
        log.debug("CustomerProductController | searchByPage | keyword: " + keyword);

        Page<Product> pageProducts = productService.search(keyword, pageNum);
        List<Product> listResult = pageProducts.getContent();

        log.debug("CustomerProductController | searchByPage | pageProducts : " + pageProducts.toString());
        log.debug("CustomerProductController | searchByPage | listResult : "  + listResult.toString());

        long startCount = (pageNum - 1) * ProductServiceImp.SEARCH_RESULTS_PER_PAGE + 1;
        long endCount = startCount + ProductServiceImp.SEARCH_RESULTS_PER_PAGE - 1;


        log.debug("CustomerProductController | searchByPage | startCount : " + startCount);
        log.debug("CustomerProductController | searchByPage | endCount : " + endCount);

        if (endCount > pageProducts.getTotalElements()) {

            log.debug("CustomerProductController | searchByPage | endCount > pageProducts.getTotalElements() | endCount : " + endCount);
            log.debug("CustomerProductController | searchByPage | endCount > pageProducts.getTotalElements() | pageProducts.getTotalElements() : " + pageProducts.getTotalElements());
            log.debug("CustomerProductController | searchByPage | endCount > pageProducts.getTotalElements()");

            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("pageTitle", keyword + " - Search Result");
        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);

        return "customer/products/search_result";
    }


}
