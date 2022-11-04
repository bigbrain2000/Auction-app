package com.example.auction.exceptions;

/**
 * Exception thrown when the username entered already exists in the database.
 */
public class EmailAlreadyExistsException extends Exception {

    public EmailAlreadyExistsException(String message) {
        super(String.format("Email %s already exists!", message));
    }
}
