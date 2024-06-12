package org.fgreau.altenshop.exception;

/**
 * Custom exception to handle the 404 error.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructor.
     * @param message message to display
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
