package com.molla.controller.admin;

import com.molla.exciptions.ProductNotFoundException;
import com.molla.helper.ProductSaveHelper;
import com.molla.model.Brand;
import com.molla.model.Category;
import com.molla.model.MollaUserDetails;
import com.molla.model.Product;
import com.molla.service.BrandService;
import com.molla.service.CategoryService;
import com.molla.service.ProductService;
import com.molla.service.serviceImp.BrandServiceImp;
import com.molla.service.serviceImp.ProductServiceImp;
import com.molla.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private String defaultRedirectURL = "redirect:/admin/products";

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String listAll(Model model) {
        log.debug("ProductController | listFirstPage is started");
        return listByPage(1, "name", "asc", null, 0, model);
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword,
                             @RequestParam("categoryId") Integer categoryId,
                             Model model) {

        log.debug("ProductController | listByPage is started");
        Page<Product> productPage = productService.findAllByPage(pageNum, sortField, sortDir, keyword, categoryId);
        List<Product> products = productPage.getContent();
        List<Category> categories = categoryService.findAllUsedInForm();

        long startCount = (pageNum - 1) * ProductServiceImp.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + BrandServiceImp.BRANDS_PER_PAGE - 1;

        if (endCount > productPage.getTotalPages()) {
            endCount = productPage.getTotalPages();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", "/admin/products");

        return "admin/products/index";

    }

    @GetMapping("/new")
    public String newProduct(Model model) {

        log.debug("ProductController | newProduct is started");

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        log.debug("ProductController | newProduct ");
        log.debug("ProductController | newProduct | listBrands : " + brandService.findAll().size());
        log.debug("ProductController | newProduct | listCategories : " + categoryService.findAll().size());

        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("categories", categoryService.findAllUsedInForm());
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "admin/products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal MollaUserDetails loggedUser,
                              RedirectAttributes ra) throws IOException {

        log.debug("ProductController | saveProduct is started");
        log.debug("ProductController | saveProduct is started | loggedUser: " + loggedUser.getUsername());

        log.debug("ProductController | saveProduct | mainImageMultipart.isEmpty() : " + mainImageMultipart.isEmpty());
        log.debug("ProductController | saveProduct | imageIDs : " + imageIDs);
        log.debug("ProductController | saveProduct | imageNames : " + imageNames);

        log.debug("ProductController | saveProduct | extraImageMultiparts size : " + extraImageMultiparts.length);


        ProductSaveHelper.setMainImageName(mainImageMultipart, product);

        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);

        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);

        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

        ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);

        ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");

        log.debug("ProductController | saveProduct | product: " + product.getName() + " saved");
        ra.addFlashAttribute("messageSuccess", "The product has been saved successfully.");

        return defaultRedirectURL;
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id,
                                             @PathVariable("status") Boolean enabled,
                                              Model model, RedirectAttributes redirectAttributes) {
        log.debug("ProductController | updateProductEnabledStatus is started");

        productService.updateEnabledStatus(id, enabled);
        log.debug("ProductController | updateProductEnabledStatus | update product with id: " + id + "to: " + enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The Product ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("messageSuccess", message);

        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        log.debug("ProductController | deleteProduct is started");

        try {
            productService.delete(id);

            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;

            log.debug("ProductController | deleteProduct | productExtraImagesDir : " + productExtraImagesDir);
            log.debug("ProductController | deleteProduct | productImagesDir : " + productImagesDir);

            FileUploadUtil.removeDir(productExtraImagesDir);

            FileUploadUtil.removeDir(productImagesDir);

            log.debug("ProductController | deleteProduct is done");

            redirectAttributes.addFlashAttribute("messageSuccess",
                    "The product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException ex) {

            log.debug("ProductController | deleteProduct | messageError : " + ex.getMessage());

            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }


        return defaultRedirectURL;
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes ra,
                              @AuthenticationPrincipal MollaUserDetails loggedUser
    ) {

        log.debug("ProductController | editProduct is started");

        try {
            Product product = productService.findById(id);
            List<Brand> listBrands = brandService.findAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            log.debug("ProductController | editProduct | loggedUser  : " + loggedUser.toString());

            boolean isReadOnlyForSalesperson = false;

            log.debug("ProductController | editProduct | product  : " + product.toString());
            log.debug("ProductController | editProduct | listBrands : " + listBrands.toString());
            log.debug("ProductController | editProduct | numberOfExistingExtraImages : " + numberOfExistingExtraImages);

            model.addAttribute("product", product);
            model.addAttribute("brands", brandService.findAll());
            model.addAttribute("categories", categoryService.findAllUsedInForm());
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);


            return "admin/products/product_form";

        } catch (ProductNotFoundException e) {

            log.debug("ProductController | editProduct | error : " + e.getMessage());

            ra.addFlashAttribute("messageError", e.getMessage());

            return defaultRedirectURL;
        }
    }


    @GetMapping("/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes ra) {

        log.debug("ProductController | viewProductDetails is started");

        try {
            Product product = productService.findById(id);

            log.debug("ProductController | viewProductDetails  | product : " + product.toString());

            model.addAttribute("product", product);

            return "admin/products/product_detail_modal";

        } catch (ProductNotFoundException e) {

            log.debug("ProductController | viewProductDetails  | messageError : " + e.getMessage());

            ra.addFlashAttribute("messageError", e.getMessage());

            return defaultRedirectURL;
        }
    }



}
