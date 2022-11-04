package com.example.auction.exceptions;

/**
 * Exception thrown when the entered password does not fulfil the requirements.
 */
public class WeakPasswordException extends Exception {

    public WeakPasswordException(String message) {
        super(String.format("Password does not contain at least %s !", message));
    }
}