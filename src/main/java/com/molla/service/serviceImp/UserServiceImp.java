package com.molla.service.serviceImp;

import com.molla.exciptions.UserNotFoundException;
import com.molla.model.User;
import com.molla.repository.UserRepository;
import com.molla.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImp implements UserService {

    public static final int USERS_PER_PAGE = 4;

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }


    @Override
    public Page<User> findAllByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1 , USERS_PER_PAGE, sort);

        if (keyword == null) {
            return userRepository.findAll(pageable);
        } else {
            return userRepository.findAll(keyword.toLowerCase(), pageable);
        }
    }

    @Override
    public boolean isEmailUnique(Integer id, String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {
            return true;
        } else {
            if (user.get().getId() == id) {
                return true;
            }
            return false;
        }
    }

    @Override
    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        return userRepository.save(user);
    }

    @Override
    public User findById(Integer id) throws UserNotFoundException {
         return userRepository.findById(id).
                 orElseThrow(() -> new UserNotFoundException("Could not find any user with ID " + id));
        }

    @Override
    public User deleteById(Integer id) throws UserNotFoundException {
        User user = findById(id);
        if (user != null) userRepository.deleteById(id);
        return user;
    }

    @Override
    public void updateEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException {
        User user = findById(id);
        user.setEnabled(enabled);
        save(user);
    }

    @Override
    public User findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("This user not found"));
    }


}
