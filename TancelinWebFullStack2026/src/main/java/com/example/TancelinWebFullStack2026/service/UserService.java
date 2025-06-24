package com.example.TancelinWebFullStack2026.service;

import com.example.TancelinWebFullStack2026.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    boolean verifyUser(String email, String password);

    boolean checkUserNameExists(String email);

    boolean createUser(User user);

    String generateToken(String email, String password);

    Optional<User> findById(Long id);

    List<User> findAll();

    boolean updateUser(Long id, User userData);

    boolean deleteUser(Long id);
}
