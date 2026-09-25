package edu.plapoly.todo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

/**
 * Catches exceptions that bubble up from controllers/services and turns them into
 * friendly, on-brand error pages instead of raw stack traces — logging the real
 * detail server-side for debugging.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        log.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        model.addAttribute("title", "Not found");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public String handleDuplicateAccount(DuplicateAccountException ex, Model model) {
        log.info("Registration rejected: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "auth/register";
    }

    @ExceptionHandler({SQLException.class, org.springframework.dao.DataAccessException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleDatabaseError(Exception ex, Model model, HttpServletRequest request) {
        log.error("Database error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("title", "We're having trouble reaching the database");
        model.addAttribute("message",
                "Please try again in a moment. If this keeps happening, let your system administrator know.");
        return "error/generic";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadInput(IllegalArgumentException ex, Model model, HttpServletRequest request) {
        log.warn("Bad request at {}: {}", request.getRequestURI(), ex.getMessage());
        model.addAttribute("title", "That didn't look right");
        model.addAttribute("message", ex.getMessage());
        return "error/generic";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception ex, Model model, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("title", "Something went wrong on our end");
        model.addAttribute("message",
                "This has been logged. Please try again, and contact support if the problem continues.");
        return "error/generic";
    }
}
