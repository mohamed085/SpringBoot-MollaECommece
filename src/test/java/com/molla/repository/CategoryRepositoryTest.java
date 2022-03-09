package com.molla.repository;

import com.molla.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repo;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Electronics");
        Category savedCategory = repo.save(category);

        System.out.println(savedCategory.toString());
        assertNotNull(savedCategory);
    }

    @Test
    public void testCreateSubCategory() {

        Category parent = new Category(4);
        Category subCategory = new Category("Memory", parent);
        Category savedCategory = repo.save(subCategory);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateMultiSubCategory() {

        Category parent = new Category(5);
        Category subCategory1 = new Category("Phones", parent);
        Category subCategory2 = new Category("Cameras", parent);

        repo.saveAll(List.of(subCategory1, subCategory2));

    }

    @Test
    public void testGetCategory() {
        Category category = repo.findById(1).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();

        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }

        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories() {
        Iterable<Category> categories = repo.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }

    @Test
    public void testListRootCategories() {
        List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending()     );
        rootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName() {
        String name = "Computers"; // Computers1
        Category category = repo.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    public void testFindByAlias() {
        String alias = "Electronics";
        Category category = repo.findByAlias(alias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }

    @Test
    public void testListEnabledCategories() {
        List<Category> categories = repo.findAllEnabled();
        categories.forEach(category -> {
            System.out.println(category.getName() + " (" + category.isEnabled() + ")");
        });
    }

    @Test
    public void testFindCategoryByAlias() {
        String alias = "electronics";
        Category category = repo.findByAliasEnabled(alias);

        assertThat(category).isNotNull();
    }


}