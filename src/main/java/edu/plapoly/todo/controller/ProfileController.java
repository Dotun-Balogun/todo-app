package edu.plapoly.todo.controller;

import edu.plapoly.todo.model.User;
import edu.plapoly.todo.security.CurrentUserResolver;
import edu.plapoly.todo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    public ProfileController(UserService userService, CurrentUserResolver currentUserResolver) {
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserResolver.resolve(authentication));
        return "profile";
    }

    @PostMapping
    public String updateProfile(Authentication authentication,
                                 @RequestParam String fullName,
                                 @RequestParam(required = false) String email,
                                 Model model) {
        User user = currentUserResolver.resolve(authentication);
        try {
            userService.updateProfile(user, fullName, email);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("user", user);
            return "profile";
        }
        model.addAttribute("successMessage", "Your profile has been updated.");
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/password")
    public String changePassword(Authentication authentication,
                                  @RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmNewPassword,
                                  Model model) {
        User user = currentUserResolver.resolve(authentication);
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("errorMessage", "New passwords don't match.");
            model.addAttribute("user", user);
            return "profile";
        }
        try {
            userService.changePassword(user, currentPassword, newPassword);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("user", user);
            return "profile";
        }
        model.addAttribute("successMessage", "Your password has been changed.");
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/theme")
    @ResponseBody
    public String toggleTheme(Authentication authentication, @RequestParam boolean darkMode) {
        User user = currentUserResolver.resolve(authentication);
        userService.setDarkModePreference(user, darkMode);
        return "ok";
    }
}
