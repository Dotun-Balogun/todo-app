package edu.plapoly.todo.exception;

/** Thrown when a requested Task/Category/Subtask doesn't exist or doesn't belong to the current user. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
