package edu.plapoly.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Plateau State Polytechnic To-Do List
 * Task Management System.
 *
 * Runs against MySQL locally (profile: dev) and Postgres in
 * production on Render (profile: prod). See application-dev.properties
 * and application-prod.properties.
 */
@SpringBootApplication
public class TodoAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
    }
}
