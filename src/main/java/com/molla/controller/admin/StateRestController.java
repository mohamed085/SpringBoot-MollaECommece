package com.molla.controller.admin;

import com.molla.dto.StateDTO;
import com.molla.model.Country;
import com.molla.model.State;
import com.molla.repository.StateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class StateRestController {

    private final StateRepository repo;

    public StateRestController(StateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/states/list_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {

        log.debug("StateRestController | listByCountry is called");

        log.debug("StateRestController | listByCountry | countryId : " + countryId);

        List<State> listStates = repo.findByCountryOrderByNameAsc(new Country(countryId));

        log.debug("StateRestController | listByCountry | listStates.size() : " + listStates.size());

        List<StateDTO> result = new ArrayList<>();

        for (State state : listStates) {
            result.add(new StateDTO(state.getId(), state.getName()));
        }

        log.debug("StateRestController | listByCountry | result : " + result.toString());

        return result;
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state) {
        State savedState = repo.save(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/states/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        repo.deleteById(id);
    }

    @PostMapping("/states/check_unique")
    @ResponseBody
    public String checkUnique(@RequestBody Map<String,String> data) {

        String name = data.get("name");

        log.debug("StateRestController | checkUnique is called");

        log.debug("StateRestController | checkUnique | name : " + name);

        State countryByName = repo.findByName(name);
        boolean isCreatingNew = (countryByName.getId() != null ? true : false);

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
