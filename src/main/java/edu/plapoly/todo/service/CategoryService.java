package edu.plapoly.todo.service;

import edu.plapoly.todo.exception.ResourceNotFoundException;
import edu.plapoly.todo.model.Category;
import edu.plapoly.todo.model.User;
import edu.plapoly.todo.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listForUser(User user) {
        return categoryRepository.findByUserOrderByNameAsc(user);
    }

    @Transactional
    public Category create(User user, String name, String colorCode) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        Category category = new Category(name.trim(), (colorCode == null || colorCode.isBlank()) ? "#8A8578" : colorCode, user);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(User user, Long categoryId) {
        Category category = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));
        categoryRepository.delete(category);
    }
}
