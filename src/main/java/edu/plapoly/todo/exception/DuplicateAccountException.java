package edu.plapoly.todo.exception;

/** Thrown when a registration attempt uses a username or email that's already taken. */
public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String message) {
        super(message);
    }
}
