package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByEmail(principal.getName()).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void updateCurrentUser(Principal principal, User updated) {
        User current = getCurrentUser(principal);
        if (current == null) return;

        current.setFirstName(updated.getFirstName());
        current.setLastName(updated.getLastName());
        current.setEmail(updated.getEmail());
        userRepository.save(current);
    }

    public void deleteCurrentUser(Principal principal) {
        User current = getCurrentUser(principal);
        if (current != null) {
            userRepository.delete(current);
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUserById(Long id) {
        User user = getUserById(id);
        if (user.getRoles().contains("ADMIN")) {
            throw new RuntimeException("Admin user cannot be deleted");
        }
        userRepository.delete(user);
    }

    public void updateUserById(Long id, User updated) {
        User user = getUserById(id);
        if (user.getRoles().contains("ADMIN")) {
            throw new RuntimeException("Admin user cannot be updated");
        }

        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setEmail(updated.getEmail());
        user.setRoles(updated.getRoles());

        userRepository.save(user);
    }

    public void registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
