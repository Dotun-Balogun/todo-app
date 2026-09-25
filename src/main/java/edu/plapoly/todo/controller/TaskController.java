package edu.plapoly.todo.controller;

import edu.plapoly.todo.model.Priority;
import edu.plapoly.todo.model.Task;
import edu.plapoly.todo.model.User;
import edu.plapoly.todo.security.CurrentUserResolver;
import edu.plapoly.todo.service.CategoryService;
import edu.plapoly.todo.service.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final CurrentUserResolver currentUserResolver;

    public TaskController(TaskService taskService, CategoryService categoryService,
                           CurrentUserResolver currentUserResolver) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/new")
    public String newTaskForm(Authentication authentication, Model model) {
        User user = currentUserResolver.resolve(authentication);
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.listForUser(user));
        model.addAttribute("priorities", Priority.values());
        return "task/form";
    }

    @PostMapping
    public String create(Authentication authentication,
                          @RequestParam String title,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reminderAt,
                          @RequestParam(required = false) Priority priority,
                          @RequestParam(required = false) Long categoryId,
                          Model model) {
        User user = currentUserResolver.resolve(authentication);
        try {
            taskService.create(user, title, description, dueDate, reminderAt, priority, categoryId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.listForUser(user));
            model.addAttribute("priorities", Priority.values());
            return "task/form";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/edit")
    public String editTaskForm(Authentication authentication, @PathVariable Long id, Model model) {
        User user = currentUserResolver.resolve(authentication);
        Task task = taskService.getOwnedTask(user, id);
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.listForUser(user));
        model.addAttribute("priorities", Priority.values());
        return "task/form";
    }

    @PostMapping("/{id}")
    public String update(Authentication authentication, @PathVariable Long id,
                          @RequestParam String title,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reminderAt,
                          @RequestParam(required = false) Priority priority,
                          @RequestParam(required = false) Long categoryId,
                          Model model) {
        User user = currentUserResolver.resolve(authentication);
        try {
            taskService.update(user, id, title, description, dueDate, reminderAt, priority, categoryId);
        } catch (IllegalArgumentException ex) {
            Task task = taskService.getOwnedTask(user, id);
            model.addAttribute("task", task);
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("categories", categoryService.listForUser(user));
            model.addAttribute("priorities", Priority.values());
            return "task/form";
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/{id}/complete")
    public String complete(Authentication authentication, @PathVariable Long id,
                            @RequestParam(required = false) String redirectTo) {
        User user = currentUserResolver.resolve(authentication);
        taskService.markComplete(user, id);
        return "redirect:" + (redirectTo != null ? redirectTo : "/dashboard");
    }

    @PostMapping("/{id}/incomplete")
    public String incomplete(Authentication authentication, @PathVariable Long id,
                              @RequestParam(required = false) String redirectTo) {
        User user = currentUserResolver.resolve(authentication);
        taskService.markIncomplete(user, id);
        return "redirect:" + (redirectTo != null ? redirectTo : "/dashboard");
    }

    @PostMapping("/{id}/delete")
    public String delete(Authentication authentication, @PathVariable Long id) {
        User user = currentUserResolver.resolve(authentication);
        taskService.delete(user, id);
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String viewTask(Authentication authentication, @PathVariable Long id, Model model) {
        User user = currentUserResolver.resolve(authentication);
        model.addAttribute("task", taskService.getOwnedTask(user, id));
        return "task/detail";
    }

    // --- Subtasks / checklist ---

    @PostMapping("/{id}/subtasks")
    public String addSubtask(Authentication authentication, @PathVariable Long id, @RequestParam String title) {
        User user = currentUserResolver.resolve(authentication);
        taskService.addSubtask(user, id, title);
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/{id}/subtasks/{subtaskId}/toggle")
    public String toggleSubtask(Authentication authentication, @PathVariable Long id, @PathVariable Long subtaskId) {
        User user = currentUserResolver.resolve(authentication);
        taskService.toggleSubtask(user, id, subtaskId);
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/{id}/subtasks/{subtaskId}/delete")
    public String deleteSubtask(Authentication authentication, @PathVariable Long id, @PathVariable Long subtaskId) {
        User user = currentUserResolver.resolve(authentication);
        taskService.deleteSubtask(user, id, subtaskId);
        return "redirect:/tasks/" + id;
    }
}
