package com.example.auction.service.confirmationtoken;

import com.example.auction.model.ConfirmationToken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Interface used for declaring the methods signatures that can be performed with a {@link ConfirmationToken} entity.
 */
public interface ConfirmationTokenService {

    /**
     * Set a token confirmation date
     *
     * @param token - the token we want to save
     * @return - an integer
     */
    int setConfirmedAt(@NotBlank String token);

    /**
     * ]
     * Save a token after it has been confirmed
     *
     * @param token -  the token we want to save
     * @return - the saved token
     */
    ConfirmationToken saveConfirmationToken(@NotNull ConfirmationToken token);

    /**
     * Get a saved token from the DB
     *
     * @param token - the token we want to search
     * @return - the persisted searched token
     */
    Optional<ConfirmationToken> getToken(@NotBlank String token);

    /**
     * Delete a token based on his id
     *
     * @param id - the token id we want to delete
     */
    void delete(@NotNull Integer id);
}
