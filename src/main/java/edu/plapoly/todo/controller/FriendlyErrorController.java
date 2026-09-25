package edu.plapoly.todo.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring's @ControllerAdvice only catches exceptions thrown inside a mapped controller
 * method. Errors before that (no matching route -> 404, method not allowed, etc.) go
 * through here instead, so those also get a friendly, on-brand page rather than the
 * default Whitelabel Error Page.
 */
@Controller
public class FriendlyErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        if (status == 404) {
            model.addAttribute("title", "Page not found");
            model.addAttribute("message", "The page you're looking for doesn't exist or may have moved.");
            return "error/404";
        }

        model.addAttribute("title", "Something went wrong");
        model.addAttribute("message", "Please try again. If this keeps happening, contact support.");
        return "error/generic";
    }
}
