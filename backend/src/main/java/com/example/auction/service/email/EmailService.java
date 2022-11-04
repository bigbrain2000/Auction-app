package com.example.auction.service.email;

import javax.validation.constraints.NotBlank;

/**
 * Interface used for declaring the methods signature that can process emails.
 */
public interface EmailService {

    /**
     *
     * @param to - addressee
     * @param body - the email we want to send
     */
    void send(@NotBlank String to, @NotBlank String body);
}
