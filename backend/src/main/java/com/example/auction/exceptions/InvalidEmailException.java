package com.example.auction.exceptions;

/**
 * Exception thrown when an email is invalid.
 */
public class InvalidEmailException extends Exception {

    public InvalidEmailException(String message) {
        super(String.format("Email %s is not valid!", message));
    }
}
