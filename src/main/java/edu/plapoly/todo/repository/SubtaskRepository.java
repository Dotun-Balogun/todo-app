package edu.plapoly.todo.repository;

import edu.plapoly.todo.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
}
