package edu.plapoly.todo.controller;

import edu.plapoly.todo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Incorrect username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("infoMessage", "You've been signed out.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String confirmPassword,
                            @RequestParam String fullName,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String matricNumber,
                            Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords don't match. Please re-enter them.");
            return "auth/register";
        }
        try {
            // DuplicateAccountException is also caught by GlobalExceptionHandler as a fallback,
            // but we catch validation issues here so the form re-renders with the user's input intact.
            userService.register(username, password, fullName, email, matricNumber);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("matricNumber", matricNumber);
            return "auth/register";
        }
        model.addAttribute("registered", true);
        return "auth/login";
    }
}
