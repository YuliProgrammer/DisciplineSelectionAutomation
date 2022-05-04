package com.discipline.selection.automation.exceptions;

/**
 * This exception is thrown when input data is invalid
 *
 * @author Yuliia_Dolnikova
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
