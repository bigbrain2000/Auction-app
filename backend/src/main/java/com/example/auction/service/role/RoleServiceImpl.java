package com.example.auction.service.role;


import com.example.auction.model.Role;
import com.example.auction.repository.RoleRepository;
import org.springframework.stereotype.Service;

/**
 * Service class used for managing roles and implementing the {@link RoleService} interface.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
