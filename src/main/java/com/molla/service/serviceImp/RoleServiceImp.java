package com.molla.service.serviceImp;

import com.molla.model.Role;
import com.molla.repository.RoleRepository;
import com.molla.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImp implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }
}
