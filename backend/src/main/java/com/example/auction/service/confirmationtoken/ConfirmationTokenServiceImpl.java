package com.example.auction.service.confirmationtoken;


import com.example.auction.exceptions.ResourceNotFoundException;
import com.example.auction.model.ConfirmationToken;
import com.example.auction.repository.ConfirmationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Concrete class that implements the {@link ConfirmationTokenService} interface.
 */
@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationTokenServiceImpl.class);

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token) {
        LOGGER.debug("Token was successfully inserted in the database");
        return confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        LOGGER.debug("Token was successfully retrieved from the database");
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        LOGGER.debug("Token was successfully updated in the database");
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

    public void delete(Integer id) {
        confirmationTokenRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Token could not be deleted from the database");
            return new ResourceNotFoundException("Token", "id", id);
        });

        LOGGER.debug("Token successfully deleted from the database");
        confirmationTokenRepository.deleteById(id);
    }
}
