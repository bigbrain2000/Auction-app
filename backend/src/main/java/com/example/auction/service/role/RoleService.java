package com.example.auction.service.role;


import com.example.auction.model.Role;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Role} entity.
 */
@Repository
public interface RoleService {

    /**
     * Save a role in the DB
     *
     * @param role -  the role we want to save into the DB
     * @return - the saved tole
     */
    Role save(@NotNull Role role);
}
