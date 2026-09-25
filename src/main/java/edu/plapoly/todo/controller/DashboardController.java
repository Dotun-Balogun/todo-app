package edu.plapoly.todo.controller;

import edu.plapoly.todo.model.*;
import edu.plapoly.todo.security.CurrentUserResolver;
import edu.plapoly.todo.service.CategoryService;
import edu.plapoly.todo.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final CurrentUserResolver currentUserResolver;

    public DashboardController(TaskService taskService, CategoryService categoryService,
                                CurrentUserResolver currentUserResolver) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication,
                             @RequestParam(required = false) String q,
                             @RequestParam(required = false) TaskStatus status,
                             @RequestParam(required = false) Long categoryId,
                             @RequestParam(required = false, defaultValue = "ALL") TaskService.QuickFilter view,
                             @RequestParam(required = false, defaultValue = "DUE_DATE_ASC") TaskService.SortOption sort,
                             @RequestParam(required = false, defaultValue = "0") int page,
                             Model model) {
        User user = currentUserResolver.resolve(authentication);

        // Keep OVERDUE statuses accurate every time the dashboard loads.
        taskService.refreshOverdueStatuses(user);

        Page<Task> tasks = taskService.search(user, q, status, categoryId, view, sort, page);
        List<Category> categories = categoryService.listForUser(user);
        List<Task> dueReminders = taskService.findDueReminders(user);

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categories);
        model.addAttribute("dueReminders", dueReminders);

        model.addAttribute("totalCount", taskService.countAll(user));
        model.addAttribute("pendingCount", taskService.countByStatus(user, TaskStatus.PENDING));
        model.addAttribute("completedCount", taskService.countByStatus(user, TaskStatus.COMPLETED));
        model.addAttribute("overdueCount", taskService.countByStatus(user, TaskStatus.OVERDUE));

        // Echo the current filter/sort state back so the UI can reflect active selections.
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedView", view);
        model.addAttribute("selectedSort", sort);

        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("sortOptions", TaskService.SortOption.values());

        return "dashboard";
    }
}
