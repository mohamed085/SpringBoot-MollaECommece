package com.molla.service;

import com.molla.exciptions.CategoryNotFoundException;
import com.molla.model.Category;
import com.molla.util.CategoryPageInfo;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findAllByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir, String keyword);

    List<Category> findAllUsedInForm();

    Category save(Category category);

    Category findById(Integer id) throws CategoryNotFoundException;

    String checkUnique(Integer id, String name, String alias);

    void updateCategoryEnabledStatus(Integer id, boolean enabled);

    void delete(Integer id) throws CategoryNotFoundException;

}
