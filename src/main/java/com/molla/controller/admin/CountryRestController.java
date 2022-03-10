package com.molla.controller.admin;

import com.molla.model.Country;
import com.molla.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/countries")
public class CountryRestController {

    private final CountryRepository repo;

    public CountryRestController(CountryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/list")
    public List<Country> listAll() {
        return repo.findAllByOrderByNameAsc();
    }

    @PostMapping("/save")
    public String save(@RequestBody Country country) {
        Country savedCountry = repo.save(country);

        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        repo.deleteById(id);
    }

    @PostMapping("/check_unique")
    @ResponseBody
    public String checkUnique(@RequestBody Map<String,String> data) {

        String name = data.get("name");

        log.debug("CountryRestController | checkUnique is called");

        log.debug("CountryRestController | checkUnique | name : " + name);

        Country countryByName = repo.findByName(name);

        boolean isCreatingNew = true;

        if (countryByName == null) {
            isCreatingNew = false;
        }

        if (isCreatingNew) {
            if (countryByName != null) return "Duplicate";
        } else {
            if (countryByName != null && countryByName.getId() != null) {
                return "Duplicate";
            }
        }

        return "OK";
    }

}
