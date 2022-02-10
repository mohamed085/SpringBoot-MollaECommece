package com.molla.service.serviceImp;

import com.molla.exciptions.BrandNotFoundException;
import com.molla.model.Brand;
import com.molla.repository.BrandRepository;
import com.molla.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImp implements BrandService {

    public static final int BRANDS_PER_PAGE = 4;

    private final BrandRepository brandRepository;

    public BrandServiceImp(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Page<Brand> findAllByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);

        if (keyword != null) return brandRepository.findAll(keyword, pageable);
        else return brandRepository.findAll(pageable);
    }

    @Override
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand findById(Integer id) throws BrandNotFoundException {
        return brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException("Could not find any brand with ID " + id));
    }

    @Override
    public String checkUnique(Integer id, String name) {
        boolean isNew = (id == null || id == 0);
        Brand brand = brandRepository.findByName(name);

        if (isNew) {
            if (brand != null) return "Duplicate";
        } else {
            if (brand != null && brand.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    @Override
    public void delete(Integer id) throws BrandNotFoundException {
        findById(id);
        brandRepository.deleteById(id);
    }
}
