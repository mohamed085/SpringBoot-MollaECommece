package com.molla.repository;

import com.molla.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', lower(u.email), ' ', lower(u.firstName), ' ', lower(u.lastName), ' ', lower(u.enabled)) LIKE %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);
}
