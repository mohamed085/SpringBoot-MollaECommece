package com.molla.controller.admin;


import com.molla.dto.CategoryDTO;
import com.molla.exciptions.BrandNotFoundException;
import com.molla.exciptions.BrandNotFoundResetException;
import com.molla.model.Brand;
import com.molla.model.Category;
import com.molla.service.BrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/brands")
public class BrandResetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandResetController.class);

    private final BrandService brandService;

    public BrandResetController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping("/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
        LOGGER.info("BrandResetController | checkUnique");
        return brandService.checkUnique(id, name);
    }

    @GetMapping("/{id}/categories")
    private List<CategoryDTO> categoriesByBrand(@PathVariable(name = "id") Integer id) throws BrandNotFoundResetException {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();

       if (id != null) {
           try {
               Brand brand = brandService.findById(id);
               Set<Category> categories = brand.getCategories();

               for (Category category : categories) {
                   CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
                   categoryDTOList.add(categoryDTO);
               }

               return categoryDTOList;
           } catch (BrandNotFoundException e) {
               throw new BrandNotFoundResetException();
           }
       } else {
           return null;
       }
    }

}
