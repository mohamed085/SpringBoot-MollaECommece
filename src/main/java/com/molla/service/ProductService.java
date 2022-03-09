package com.molla.service;


import com.molla.exciptions.ProductNotFoundException;
import com.molla.model.Brand;
import com.molla.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Page<Product> findAllByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId);

    Product save(Product product);

    String checkUnique(Integer id, String name);

    void updateEnabledStatus(Integer id, Boolean enabled);

    void delete(Integer id) throws ProductNotFoundException;

    Product findById(Integer id) throws ProductNotFoundException;

    Page<Product> listByCategory(int pageNum, Integer categoryId);

    Product getByAlias(String alias) throws ProductNotFoundException;

    Page<Product> search(String keyword, int pageNum);
}
