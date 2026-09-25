package edu.plapoly.todo.controller;

import edu.plapoly.todo.model.User;
import edu.plapoly.todo.security.CurrentUserResolver;
import edu.plapoly.todo.service.ReportService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final CurrentUserResolver currentUserResolver;

    public ReportController(ReportService reportService, CurrentUserResolver currentUserResolver) {
        this.reportService = reportService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/reports")
    public String reports(Authentication authentication, Model model) {
        User user = currentUserResolver.resolve(authentication);
        model.addAttribute("breakdown", reportService.completionByCategory(user));
        model.addAttribute("totalCompleted", reportService.totalCompleted(user));
        model.addAttribute("totalTasks", reportService.totalTasks(user));
        return "reports";
    }
}
