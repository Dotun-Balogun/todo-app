package edu.plapoly.todo.service;

import edu.plapoly.todo.exception.ResourceNotFoundException;
import edu.plapoly.todo.model.*;
import edu.plapoly.todo.repository.CategoryRepository;
import edu.plapoly.todo.repository.SubtaskRepository;
import edu.plapoly.todo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private static final int PAGE_SIZE = 10;

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final SubtaskRepository subtaskRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository,
                        SubtaskRepository subtaskRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.subtaskRepository = subtaskRepository;
    }

    /** Sort options exposed to the dashboard UI. */
    public enum SortOption {
        DUE_DATE_ASC, DUE_DATE_DESC, REMINDER_ASC, PRIORITY_DESC, PRIORITY_ASC, NEWEST, TITLE_ASC
    }

    /** Quick-filter views: Today / This Week / Overdue / All. */
    public enum QuickFilter {
        ALL, TODAY, THIS_WEEK, OVERDUE
    }

    @Transactional
    public Task create(User user, String title, String description, LocalDate dueDate,
                        LocalDateTime reminderAt, Priority priority, Long categoryId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        if (title.length() > 150) {
            throw new IllegalArgumentException("Task title is too long (max 150 characters).");
        }
        if (reminderAt != null && dueDate != null && reminderAt.toLocalDate().isAfter(dueDate)) {
            throw new IllegalArgumentException("Reminder date can't be after the due date.");
        }

        Task task = new Task(title.trim(), description, dueDate, priority == null ? Priority.MEDIUM : priority, user);
        task.setReminderAt(reminderAt);

        if (categoryId != null) {
            Category category = categoryRepository.findByIdAndUser(categoryId, user)
                    .orElseThrow(() -> new ResourceNotFoundException("Selected category was not found."));
            task.setCategory(category);
        }

        Task saved = taskRepository.save(task);
        log.info("Task '{}' created for user '{}'", saved.getTitle(), user.getUsername());
        return saved;
    }

    @Transactional
    public Task update(User user, Long taskId, String title, String description, LocalDate dueDate,
                        LocalDateTime reminderAt, Priority priority, Long categoryId) {
        Task task = getOwnedTask(user, taskId);

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        if (reminderAt != null && dueDate != null && reminderAt.toLocalDate().isAfter(dueDate)) {
            throw new IllegalArgumentException("Reminder date can't be after the due date.");
        }

        task.setTitle(title.trim());
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setReminderAt(reminderAt);
        task.setPriority(priority == null ? Priority.MEDIUM : priority);

        if (categoryId == null) {
            task.setCategory(null);
        } else {
            Category category = categoryRepository.findByIdAndUser(categoryId, user)
                    .orElseThrow(() -> new ResourceNotFoundException("Selected category was not found."));
            task.setCategory(category);
        }

        return task;
    }

    @Transactional
    public void markComplete(User user, Long taskId) {
        Task task = getOwnedTask(user, taskId);
        task.setStatus(TaskStatus.COMPLETED);
        task.setDateCompleted(LocalDateTime.now());
    }

    @Transactional
    public void markIncomplete(User user, Long taskId) {
        Task task = getOwnedTask(user, taskId);
        task.setStatus(task.isOverdue() ? TaskStatus.OVERDUE : TaskStatus.PENDING);
        task.setDateCompleted(null);
    }

    @Transactional
    public void delete(User user, Long taskId) {
        Task task = getOwnedTask(user, taskId);
        taskRepository.delete(task);
        log.info("Task {} deleted by user '{}'", taskId, user.getUsername());
    }

    public Task getOwnedTask(User user, Long taskId) {
        return taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found, or it doesn't belong to you."));
    }

    /** Main dashboard query: search + filter + sort + pagination, all in one call. */
    public Page<Task> search(User user, String keyword, TaskStatus status, Long categoryId,
                              QuickFilter quickFilter, SortOption sortOption, int page) {
        LocalDate fromDate = null;
        LocalDate toDate = null;
        TaskStatus effectiveStatus = status;

        LocalDate today = LocalDate.now();
        if (quickFilter != null) {
            switch (quickFilter) {
                case TODAY -> { fromDate = today; toDate = today; }
                case THIS_WEEK -> { fromDate = today; toDate = today.plusDays(7); }
                case OVERDUE -> { toDate = today.minusDays(1); effectiveStatus = null; }
                default -> {}
            }
        }

        Pageable pageable;
        boolean isPriorityDesc = (sortOption == SortOption.PRIORITY_DESC);
        boolean isPriorityAsc = (sortOption == SortOption.PRIORITY_ASC);

        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Page<Task> results;

        if (isPriorityDesc || isPriorityAsc) {
            // Priority is stored as text (HIGH/MEDIUM/LOW), which sorts alphabetically by
            // default and gives the wrong order. These two queries use an explicit CASE
            // expression instead, so pagination is unsorted here (order comes from the query).
            pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE);
            results = isPriorityDesc
                    ? taskRepository.searchOrderByPriorityDesc(user, normalizedKeyword, effectiveStatus, categoryId, fromDate, toDate, pageable)
                    : taskRepository.searchOrderByPriorityAsc(user, normalizedKeyword, effectiveStatus, categoryId, fromDate, toDate, pageable);
        } else {
            Sort sort = switch (sortOption == null ? SortOption.DUE_DATE_ASC : sortOption) {
                case DUE_DATE_ASC -> Sort.by(Sort.Direction.ASC, "dueDate");
                case DUE_DATE_DESC -> Sort.by(Sort.Direction.DESC, "dueDate");
                case REMINDER_ASC -> Sort.by(Sort.Direction.ASC, "reminderAt");
                case TITLE_ASC -> Sort.by(Sort.Direction.ASC, "title");
                case NEWEST -> Sort.by(Sort.Direction.DESC, "dateCreated");
                default -> Sort.by(Sort.Direction.ASC, "dueDate");
            };
            pageable = PageRequest.of(Math.max(page, 0), PAGE_SIZE, sort);
            results = taskRepository.search(user, normalizedKeyword, effectiveStatus, categoryId, fromDate, toDate, pageable);
        }

        // OVERDUE quick filter needs "not completed" applied post-query since it isn't a single status equality.
        if (quickFilter == QuickFilter.OVERDUE) {
            List<Task> filtered = results.getContent().stream()
                    .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                    .toList();
            return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
        }

        return results;
    }

    /** Adds a subtask/checklist item to a task. */
    @Transactional
    public void addSubtask(User user, Long taskId, String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Checklist item can't be empty.");
        }
        Task task = getOwnedTask(user, taskId);
        task.getSubtasks().add(new Subtask(title.trim(), task));
    }

    @Transactional
    public void toggleSubtask(User user, Long taskId, Long subtaskId) {
        Task task = getOwnedTask(user, taskId);
        Subtask subtask = task.getSubtasks().stream()
                .filter(s -> s.getId().equals(subtaskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found."));
        subtask.setCompleted(!subtask.isCompleted());
    }

    @Transactional
    public void deleteSubtask(User user, Long taskId, Long subtaskId) {
        Task task = getOwnedTask(user, taskId);
        task.getSubtasks().removeIf(s -> s.getId().equals(subtaskId));
    }

    /**
     * Sweeps a user's tasks and flips any whose due date has passed into OVERDUE.
     * Called on dashboard load rather than via a background scheduler, to keep the
     * app simple and stateless between requests.
     */
    @Transactional
    public void refreshOverdueStatuses(User user) {
        List<Task> overdueNow = taskRepository.findByUserAndStatusNotAndDueDateBefore(
                user, TaskStatus.COMPLETED, LocalDate.now());
        for (Task t : overdueNow) {
            if (t.getStatus() != TaskStatus.OVERDUE) {
                t.setStatus(TaskStatus.OVERDUE);
            }
        }
    }

    public List<Task> findDueReminders(User user) {
        return taskRepository.findByUserAndReminderAtIsNotNullAndStatusNot(user, TaskStatus.COMPLETED)
                .stream()
                .filter(Task::isReminderDue)
                .toList();
    }

    public long countByStatus(User user, TaskStatus status) {
        return taskRepository.countByUserAndStatus(user, status);
    }

    public long countAll(User user) {
        return taskRepository.countByUser(user);
    }
}
