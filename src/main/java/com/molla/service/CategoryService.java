package com.molla.service;

import com.molla.exciptions.CategoryNotFoundException;
import com.molla.model.Category;
import com.molla.util.CategoryPageInfo;

import java.util.List;

public interface CategoryService {

    public List<Category> findAll();

    public List<Category> findAllByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir, String keyword);

    public List<Category> findAllUsedInForm();

    public Category save(Category category);

    public Category findById(Integer id) throws CategoryNotFoundException;

    public String checkUnique(Integer id, String name, String alias);

    public void updateCategoryEnabledStatus(Integer id, boolean enabled);

    public void delete(Integer id) throws CategoryNotFoundException;

}
