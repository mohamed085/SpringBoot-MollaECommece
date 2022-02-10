package com.molla.service;

import com.molla.exciptions.BrandNotFoundException;
import com.molla.model.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {

    List<Brand> findAll();

    Page<Brand> findAllByPage(int pageNum, String sortField, String sortDir, String keyword);

    Brand save (Brand brand);

    Brand findById(Integer id) throws BrandNotFoundException;

    String checkUnique(Integer id, String name);

    void delete(Integer id) throws BrandNotFoundException;
}
