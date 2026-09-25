package edu.plapoly.todo.service;

import edu.plapoly.todo.exception.DuplicateAccountException;
import edu.plapoly.todo.exception.ResourceNotFoundException;
import edu.plapoly.todo.model.User;
import edu.plapoly.todo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String username, String rawPassword, String fullName, String email, String matricNumber) {
        username = username == null ? "" : username.trim();
        email = email == null ? null : email.trim();

        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters.");
        }
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateAccountException("That username is already taken. Try another.");
        }
        if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
            throw new DuplicateAccountException("An account with that email already exists.");
        }

        User user = new User(username, passwordEncoder.encode(rawPassword), fullName.trim(), email);
        user.setMatricNumber(matricNumber);
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Your current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public User updateProfile(User user, String fullName, String email) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        user.setFullName(fullName.trim());
        user.setEmail(email == null ? null : email.trim());
        return userRepository.save(user);
    }

    @Transactional
    public void setDarkModePreference(User user, boolean darkMode) {
        user.setDarkModePreferred(darkMode);
        userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found."));
    }
}
