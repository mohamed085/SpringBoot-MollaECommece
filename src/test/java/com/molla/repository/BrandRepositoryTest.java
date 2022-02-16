package com.molla.repository;

import com.molla.model.Brand;
import com.molla.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testCreateBrand() {
        Category category = new Category(3);
        Brand brand = new Brand("Acer");
        brand.getCategories().add(category);

        Brand savedBrand = brandRepository.save(brand);
        System.out.println(savedBrand.toString());
        assertNotNull(savedBrand);
    }

    @Test
    public void testCreateBrandWithMultiCategories() {

        Brand brand = new Brand("Samsung");
        brand.getCategories().add(new Category(13));
        brand.getCategories().add(new Category(8));
        brand.getCategories().add(new Category(7));
        brand.getCategories().add(new Category(14));

        Brand savedBrand = brandRepository.save(brand);
        System.out.println(savedBrand.toString());
        assertNotNull(savedBrand);
    }

    @Test
    public void testGetById() {
        Brand brand = brandRepository.findById(5).get();

        assertEquals(brand.getId(), 5);
    }

    @Test
    public void testUpdate() {
        Brand brand = brandRepository.findById(5).get();
        String name = "Test";
        brand.setName(name);
        Brand savedBrand = brandRepository.save(brand);

        assertEquals(savedBrand.getName(), name);
    }

    @Test
    public void testDelete() {
        brandRepository.deleteById(5);
    }
}