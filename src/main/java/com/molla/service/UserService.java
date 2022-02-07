package com.molla.service;

import com.molla.exciptions.UserNotFoundException;
import com.molla.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    public Page<User> findAllByPage(int pageNum, String sortField, String sortDir, String keyword);

    boolean isEmailUnique(Integer id, String email);

    User save(User user);

    User findById(Integer id) throws UserNotFoundException;

    User deleteById(Integer id) throws UserNotFoundException;

    void updateEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException;

    User findByEmail(String email) throws UserNotFoundException;
}
