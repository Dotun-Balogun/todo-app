package edu.plapoly.todo.repository;

import edu.plapoly.todo.model.Task;
import edu.plapoly.todo.model.TaskStatus;
import edu.plapoly.todo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUser(Long id, User user);

    long countByUserAndStatus(User user, TaskStatus status);

    long countByUser(User user);

    /**
     * Main search/filter/sort query behind the dashboard task list.
     * Any parameter left null is simply ignored (JPQL short-circuits on the OR).
     */
    @Query("""
           SELECT t FROM Task t
           WHERE t.user = :user
           AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
           AND (:status IS NULL OR t.status = :status)
           AND (:categoryId IS NULL OR t.category.id = :categoryId)
           AND (:fromDate IS NULL OR t.dueDate >= :fromDate)
           AND (:toDate IS NULL OR t.dueDate <= :toDate)
           """)
    Page<Task> search(
            @Param("user") User user,
            @Param("keyword") String keyword,
            @Param("status") TaskStatus status,
            @Param("categoryId") Long categoryId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query("""
           SELECT t FROM Task t
           WHERE t.user = :user
           AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
           AND (:status IS NULL OR t.status = :status)
           AND (:categoryId IS NULL OR t.category.id = :categoryId)
           AND (:fromDate IS NULL OR t.dueDate >= :fromDate)
           AND (:toDate IS NULL OR t.dueDate <= :toDate)
           ORDER BY CASE t.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END ASC
           """)
    Page<Task> searchOrderByPriorityDesc(
            @Param("user") User user,
            @Param("keyword") String keyword,
            @Param("status") TaskStatus status,
            @Param("categoryId") Long categoryId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @Query("""
           SELECT t FROM Task t
           WHERE t.user = :user
           AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
           AND (:status IS NULL OR t.status = :status)
           AND (:categoryId IS NULL OR t.category.id = :categoryId)
           AND (:fromDate IS NULL OR t.dueDate >= :fromDate)
           AND (:toDate IS NULL OR t.dueDate <= :toDate)
           ORDER BY CASE t.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END DESC
           """)
    Page<Task> searchOrderByPriorityAsc(
            @Param("user") User user,
            @Param("keyword") String keyword,
            @Param("status") TaskStatus status,
            @Param("categoryId") Long categoryId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    List<Task> findByUserAndStatusNotAndDueDateBefore(User user, TaskStatus excludedStatus, LocalDate date);

    List<Task> findByUserAndReminderAtIsNotNullAndStatusNot(User user, TaskStatus excludedStatus);

    List<Task> findByUser(User user);
}
