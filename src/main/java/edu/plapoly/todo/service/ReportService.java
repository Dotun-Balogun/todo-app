package edu.plapoly.todo.service;

import edu.plapoly.todo.model.Task;
import edu.plapoly.todo.model.TaskStatus;
import edu.plapoly.todo.model.User;
import edu.plapoly.todo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TaskRepository taskRepository;

    public ReportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public record CategoryBreakdown(String categoryName, String colorCode, long total, long completed) {
        public int completionPercent() {
            return total == 0 ? 0 : (int) Math.round((completed * 100.0) / total);
        }
    }

    /** Groups the user's tasks by category and reports completion rate per group. */
    public List<CategoryBreakdown> completionByCategory(User user) {
        List<Task> tasks = taskRepository.findByUser(user);

        Map<String, List<Task>> grouped = tasks.stream()
                .collect(Collectors.groupingBy(t -> t.getCategory() == null ? "Uncategorized" : t.getCategory().getName()));

        List<CategoryBreakdown> breakdown = new ArrayList<>();
        for (Map.Entry<String, List<Task>> entry : grouped.entrySet()) {
            long total = entry.getValue().size();
            long completed = entry.getValue().stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
            String color = entry.getValue().stream()
                    .map(Task::getCategory)
                    .filter(Objects::nonNull)
                    .map(c -> c.getColorCode())
                    .findFirst()
                    .orElse("#8A8578");
            breakdown.add(new CategoryBreakdown(entry.getKey(), color, total, completed));
        }
        breakdown.sort(Comparator.comparing(CategoryBreakdown::categoryName));
        return breakdown;
    }

    public long totalCompleted(User user) {
        return taskRepository.countByUserAndStatus(user, TaskStatus.COMPLETED);
    }

    public long totalTasks(User user) {
        return taskRepository.countByUser(user);
    }
}
