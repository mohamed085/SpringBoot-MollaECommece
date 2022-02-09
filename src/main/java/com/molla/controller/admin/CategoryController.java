package com.molla.controller.admin;

import com.molla.exciptions.CategoryNotFoundException;
import com.molla.exporter.CategoryCsvExporter;
import com.molla.exporter.CategoryExcelExporter;
import com.molla.exporter.CategoryPdfExporter;
import com.molla.model.Category;
import com.molla.service.CategoryService;
import com.molla.util.CategoryPageInfo;
import com.molla.util.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("")
    public String listFirstPage(Model model) {
        log.debug("CategoryController | listFirstPage is started");
        log.debug("CategoryController | listFirstPage | sortDir : " + "asc");

        return listByPage(1, "asc", null, model);
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword,
                             Model model) {

        log.debug("CategoryController | listByPage is started");

        if (sortDir ==  null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> rootCategories = categoryService.findAllByPage(pageInfo, pageNum, sortDir, keyword);


        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("categories", rootCategories);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/admin/categories");


        log.debug("CategoryController | listByPage | listCategories : " + rootCategories.toString());
        log.debug("CategoryController | listByPage | reverseSortDir : " + reverseSortDir);
        log.debug("CategoryController | listByPage | totalPages : " + pageInfo.getTotalPages() );
        log.debug("CategoryController | listByPage | totalItems : " + pageInfo.getTotalElements() );
        log.debug("CategoryController | listByPage | currentPage : " + pageNum );
        log.debug("CategoryController | listByPage | sortDir : " + sortDir);

        return "admin/categories/index";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {

        log.debug("CategoryController | newCategory is started");

        List<Category> listCategories = categoryService.findAllUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("categories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");

        log.debug("CategoryController | newCategory | listCategories : " + listCategories.toString());

        return "admin/categories/category_form";

    }

    @PostMapping("/save")
    public String saveCategory(Category category,
                               @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes ra) throws IOException {

        log.debug("CategoryController | saveCategory is started");

        log.debug("CategoryController | saveCategory | multipartFile.isEmpty() : " + multipartFile.isEmpty());

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            log.debug("CategoryController | saveCategory | fileName : " + fileName);

            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);
            String uploadDir = "category-images/" + savedCategory.getId();

            log.debug("CategoryController | saveCategory | savedCategory : " + savedCategory.toString());
            log.debug("CategoryController | saveCategory | uploadDir : " + uploadDir);

            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }

        ra.addFlashAttribute("messageSuccess", "The category has been saved successfully.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model,
                               RedirectAttributes ra) {

        log.debug("CategoryController | editCategory is started");

        try {
            Category category = categoryService.findById(id);
            List<Category> listCategories = categoryService.findAllUsedInForm();

            log.debug("CategoryController | editCategory | category : " + category.toString());
            log.debug("CategoryController | editCategory | listCategories : " + listCategories.toString());


            model.addAttribute("category", category);
            model.addAttribute("categories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

            return "admin/categories/category_form";

        } catch (CategoryNotFoundException ex) {
            log.debug("CategoryController | editCategory | messageError : " + ex.getMessage());
            ra.addFlashAttribute("messageError", ex.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {

        log.debug("CategoryController | updateCategoryEnabledStatus is started");

        categoryService.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID " + id + " has been " + status;

        log.debug("CategoryController | updateCategoryEnabledStatus | status : " + status);
        log.debug("CategoryController | updateCategoryEnabledStatus | message : " + message);

        redirectAttributes.addFlashAttribute("messageSuccess", message);

        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id,
                                 RedirectAttributes redirectAttributes) {

        log.debug("CategoryController | deleteCategory is started");

        log.debug("CategoryController | deleteCategory | id : " + id);

        try {
            categoryService.delete(id);

            log.debug("CategoryController | deleteCategory | category deleted");

            String categoryDir = "category-images/" + id;

            log.debug("CategoryController | deleteCategory | categoryDir : " + categoryDir);

            FileUpload.removeDir(categoryDir);

            log.debug("CategoryController | deleteCategory | FileUploadUtil.removeDir is over");

            log.debug("CategoryController | deleteCategory | categoryDir : " + categoryDir);

            redirectAttributes.addFlashAttribute("messageSuccess",
                    "The category ID " + id + " has been deleted successfully");


        } catch (CategoryNotFoundException ex) {
            log.debug("CategoryController | deleteCategory | messageError : " + ex.getMessage());
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {

        log.debug("CategoryController | exportToCSV is started");

        List<Category> listCategories = categoryService.findAllUsedInForm();

        log.debug("CategoryController | exportToCSV | listCategories : " + listCategories.toString());

        CategoryCsvExporter exporter = new CategoryCsvExporter();
        exporter.export(listCategories, response);

        log.debug("CategoryController | exportToCSV | export completed");

    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        log.debug("CategoryController | exportToExcel is called");

        List<Category> listCategories = categoryService.findAllUsedInForm();

        log.debug("CategoryController | exportToExcel | categoryService.listAll() : " + listCategories.size());

        CategoryExcelExporter exporter = new CategoryExcelExporter();

        log.debug("CategoryController | exportToExcel | export is starting");

        exporter.export(listCategories, response);

        log.debug("CategoryController | exportToExcel | export completed");

    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {

        log.debug("CategoryController | exportToPDF is called");

        List<Category> listCategories = categoryService.findAllUsedInForm();

        log.debug("CategoryController | exportToPDF | categoryService.listAll() : " + listCategories.size());

        CategoryPdfExporter exporter = new CategoryPdfExporter();

        log.debug("CategoryController | exportToPDF | export is starting");

        exporter.export(listCategories, response);

        log.debug("CategoryController | exportToPDF | export completed");

    }

}
