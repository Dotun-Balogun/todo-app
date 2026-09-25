package edu.plapoly.todo.controller;

import edu.plapoly.todo.model.User;
import edu.plapoly.todo.security.CurrentUserResolver;
import edu.plapoly.todo.service.CategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CurrentUserResolver currentUserResolver;

    public CategoryController(CategoryService categoryService, CurrentUserResolver currentUserResolver) {
        this.categoryService = categoryService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public String list(Authentication authentication, Model model) {
        User user = currentUserResolver.resolve(authentication);
        model.addAttribute("categories", categoryService.listForUser(user));
        return "category/list";
    }

    @PostMapping
    public String create(Authentication authentication,
                          @RequestParam String name,
                          @RequestParam(required = false) String colorCode,
                          Model model) {
        User user = currentUserResolver.resolve(authentication);
        try {
            categoryService.create(user, name, colorCode);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.listForUser(user));
            return "category/list";
        }
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(Authentication authentication, @PathVariable Long id) {
        User user = currentUserResolver.resolve(authentication);
        categoryService.delete(user, id);
        return "redirect:/categories";
    }
}
