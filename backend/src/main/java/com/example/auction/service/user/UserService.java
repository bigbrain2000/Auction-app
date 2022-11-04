package com.example.auction.service.user;

import com.example.auction.exceptions.EmailAlreadyExistsException;
import com.example.auction.exceptions.InvalidEmailException;
import com.example.auction.exceptions.WeakPasswordException;
import com.example.auction.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Interface used for declaring the methods signature that can be performed with a {@link User} entity.
 */
public interface UserService {

    /**
     * Get a list with all the trainings
     *
     * @return a list with all the trainings
     */
    List<User> findAll();

    /**
     * Find a specific user based on id.
     *
     * @param id - user id
     * @return found user.
     */
    User findById(@NotNull Integer id);

    /**
     * Find a specific user based on email
     *
     * @param email - email of user
     * @return found user.
     */
    User findByEmail(@NotBlank String email);

    /**
     * Save a user in the database
     *
     * @param user - user entity
     * @return saved user
     */
    User save(@NotNull User user);

    /**
     * Update all information for a specific user
     *
     * @param newUser - the user who will be persisted
     * @param id      - current user id
     * @return updated user.
     */
    User update(@NotNull User newUser, @NotNull Integer id);

    /**
     * Delete a user by a specific id
     *
     * @param id - persisted user id
     */
    void deleteById(@NotNull Integer id);

    /**
     * Check if the username already exists but no exception is thrown
     *
     * @param username - username of user
     * @return true if the username already exist, otherwise false
     */
    boolean isUserEmailAlreadyRegistered(@NotBlank String username) throws EmailAlreadyExistsException;

    /**
     * Verify is the user email is valid
     *
     * @param email - email of user
     * @throws InvalidEmailException - if a user email is invalid
     */
    void checkIfEmailIsValid(@NotBlank String email) throws InvalidEmailException;

    /**
     * Set user account as enabled
     *
     * @param email of user
     * @return - an integer
     */
    int enableUser(@NotBlank String email);

    /**
     * After the email is confirmed, the token will be set as confirmed the user acc will be enabled
     *
     * @param token - the unique generated token for a new user
     * @return - a confirmation message in a success scenario, else a message of failure if the 30 from registering passed
     */
    String confirmToken(@NotBlank String token);

    /**
     * Based on entered data, a new user will be saved in the DB and a token
     * is generated for validating email within the next 30 minutes from registering.
     *
     * @param request - the user who register in the app
     * @return - a String which contains the unique token generated for the registered user
     * @throws InvalidEmailException       - if a user email is invalid
     * @throws EmailAlreadyExistsException - if a user with the same email already exists
     * @throws WeakPasswordException       - if user`s password does not fulfill the criteria
     */
    String register(@NotNull User request) throws InvalidEmailException, EmailAlreadyExistsException, WeakPasswordException;
}
