package com.molla.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import com.molla.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository repo;

    @Test
    public void testCreateCountry() {
        Country country = repo.save(new Country("Egypt", "EGP"));

        assertNotNull(country);
    }

    @Test
    public void testListCountries() {
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);

        assertNotNull(listCountries);
    }

    @Test
    public void testUpdateCountry() {
        Integer id = 1;
        String name = "Egypt";

        Country country = repo.findById(id).get();
        country.setName(name);

        Country updatedCountry = repo.save(country);

        assertEquals(updatedCountry.getName(), name);
    }

    @Test
    public void testGetCountry() {
        Integer id = 1;
        Country country = repo.findById(id).get();

        System.out.println(country);
        assertNotNull(country);
    }

    @Test
    public void testDeleteCountry() {
        Integer id = 2;
        repo.deleteById(id);

        repo.findById(id);
    }
}
