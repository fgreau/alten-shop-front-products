package org.fgreau.altenshop.exception;

/**
 * Custom exception to handle the 400 error.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Constructor.
     * @param message message to display
     */
    public BadRequestException(final String message) {
        super(message);
    }
}
