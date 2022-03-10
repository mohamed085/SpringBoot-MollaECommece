package com.molla.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.molla.model.Country;
import com.molla.model.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
class StateRepositoryTest {
    private StateRepository repo;

    private TestEntityManager entityManager;

    @Autowired
    public StateRepositoryTest(StateRepository repo, TestEntityManager entityManager) {
        super();
        this.repo = repo;
        this.entityManager = entityManager;
    }

    @Test
    public void testCreateStates() {

        Integer countryId = 1;
        Country country = entityManager.find(Country.class, countryId);

		State state = repo.save(new State("Alexandria", country));
//		State state = repo.save(new State("Cairo", country));
//		State state = repo.save(new State("Monufia", country));
//      State state = repo.save(new State("Gharbia", country));

        assertNotNull(state);
    }


    @Test
    public void testListStatesByCountry() {
        Integer countryId = 1;
        Country country = entityManager.find(Country.class, countryId);
        List<State> listStates = repo.findByCountryOrderByNameAsc(country);

        listStates.forEach(System.out::println);

        assertNotNull(listStates);
    }

    @Test
    public void testUpdateState() {
        Integer stateId = 3;
        String stateName = "Giza";
        State state = repo.findById(stateId).get();

        state.setName(stateName);
        State updatedState = repo.save(state);

        assertEquals(updatedState.getName(), stateName);
    }

    @Test
    public void testGetState() {
        Integer stateId = 1;
        Optional<State> findById = repo.findById(stateId);

        assertNotNull(findById.isPresent());
    }

    @Test
    public void testDeleteState() {
        Integer stateId = 8;
        repo.deleteById(stateId);

        Optional<State> findById = repo.findById(stateId);
        assertNotNull(findById.get());
    }


}