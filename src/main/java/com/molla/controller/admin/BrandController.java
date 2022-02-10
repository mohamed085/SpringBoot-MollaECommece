package com.molla.controller.admin;

import com.molla.exciptions.BrandNotFoundException;
import com.molla.exporter.BrandCsvExporter;
import com.molla.exporter.BrandExcelExporter;
import com.molla.exporter.BrandPdfExporter;
import com.molla.model.Brand;
import com.molla.model.Category;
import com.molla.service.BrandService;
import com.molla.service.CategoryService;
import com.molla.service.serviceImp.BrandServiceImp;
import com.molla.util.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@RequestMapping("/admin/brands")
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String listAll(Model model) {
        log.debug("BrandController | listFirstPage is started");

        listByPage(1, "name", "asc", null, model);
        return "admin/brands/index";
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword,
                             Model model) {

        log.debug("BrandController | listByPage is started");
        Page<Brand> brandPage = brandService.findAllByPage(pageNum, sortField, sortDir, keyword);
        List<Brand> brands = brandPage.getContent();

        long startCount = (pageNum - 1) * BrandServiceImp.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandServiceImp.BRANDS_PER_PAGE - 1;

        if (endCount > brandPage.getTotalPages()) {
            endCount = brandPage.getTotalPages();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("brands", brands);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", brandPage.getTotalPages());
        model.addAttribute("totalItems", brandPage.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/admin/brands");

        return "admin/brands/index";

    }

    @GetMapping("/new")
    public String newBrand(Model model) {

        log.debug("BrandController | newBrand is started");

        List<Brand> brands = brandService.findAll();
        List<Category> categories = categoryService.findAllUsedInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("brands", brands);
        model.addAttribute("categoriesList", categories);
        model.addAttribute("pageTitle", "Create New Brand");

        log.debug("BrandController | newBrand | brands : " + brands.toString());

        return "admin/brands/brand_form";

    }

    @PostMapping("/save")
    public String saveCategory(Brand brand,
                               @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes ra) throws IOException {

        log.debug("BrandController | saveBrand is started");

        log.debug("BrandController | saveBrand | multipartFile.isEmpty() : " + multipartFile.isEmpty());

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            log.debug("BrandController | saveBrand | fileName : " + fileName);

            brand.setLogo(fileName);

            Brand savedBrand = brandService.save(brand);
            String uploadDir = "brand-logos/" + savedBrand.getId();

            log.debug("BrandController | saveBrand | savedBrand : " + savedBrand.toString());
            log.debug("BrandController | saveCategory | uploadDir : " + uploadDir);

            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        } else {
            brandService.save(brand);
        }

        ra.addFlashAttribute("messageSuccess", "The brand has been saved successfully.");
        return "redirect:/admin/brands";
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {

        log.debug("BrandController | exportToCSV is started");

        List<Brand> listBrands = brandService.findAll();

        log.debug("BrandController | exportToCSV | listBrands : " + listBrands.toString());

        BrandCsvExporter exporter = new BrandCsvExporter();
        exporter.export(listBrands, response);

        log.debug("BrandController | exportToCSV | export completed");

    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        log.debug("BrandController | exportToExcel is called");

        List<Brand> listBrands = brandService.findAll();

        log.debug("BrandController | exportToExcel | listBrands Size : " + listBrands.size());

        BrandExcelExporter exporter = new BrandExcelExporter();

        log.debug("BrandController | exportToExcel | export is starting");

        exporter.export(listBrands, response);

        log.debug("BrandController | exportToExcel | export completed");

    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {

        log.debug("BrandController | exportToPDF is called");

        List<Brand> listBrands = brandService.findAll();

        log.debug("BrandController | exportToPDF | listBrands Size : " + listBrands.size());

        BrandPdfExporter exporter = new BrandPdfExporter();

        log.debug("BrandController | exportToPDF | export is starting");

        exporter.export(listBrands, response);

        log.debug("BrandController | exportToPDF | export completed");

    }

    @GetMapping("/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model,
                            RedirectAttributes ra) {

        log.debug("BrandController | editBrand is started");

        try {
            Brand brand = brandService.findById(id);
            List<Category> categories = categoryService.findAllUsedInForm();

            log.debug("BrandController | editBrand | brand : " + brand.toString());
            log.debug("BrandController | editBrand | listCategories : " + categories.toString());

            model.addAttribute("categoriesList", categories);
            model.addAttribute("brand", brand);
            model.addAttribute("pageTitle", "Edit brand (ID: " + id + ")");

            return "admin/brands/brand_form";

        } catch (BrandNotFoundException ex) {

            log.debug("BrandController | editBrand | messageError : " + ex.getMessage());
            ra.addFlashAttribute("messageError", ex.getMessage());
            return "redirect:/admin/brands";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id,
                              RedirectAttributes redirectAttributes) {

        log.debug("BrandController | deleteBrand is started");
        log.debug("BrandController | deleteBrand | id : " + id);

        try {
            brandService.delete(id);

            log.debug("BrandController | deleteBrand | brand deleted");

            String brandDir = "brand-logos/" + id;

            log.debug("BrandController | deleteBrand | brandDir : " + brandDir);

            FileUpload.removeDir(brandDir);

            log.debug("BrandController | deleteBrand | FileUploadUtil.removeDir is over");

            log.debug("BrandController | deleteBrand | brandDir : " + brandDir);

            redirectAttributes.addFlashAttribute("messageSuccess",
                    "The brand ID " + id + " has been deleted successfully");

        } catch (BrandNotFoundException ex) {
            log.debug("BrandController | deleteBrand | messageError : " + ex.getMessage());
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }

        return "redirect:/admin/brands";

    }
}
