package com.shop.shopmanagement.service;

import com.shop.shopmanagement.entity.User;
import com.shop.shopmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }
        user.setPassword(hashPassword(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        user.setLocked(false);
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác!"));
        
        if (!user.getPassword().equals(hashPassword(password))) {
            throw new IllegalArgumentException("Tài khoản hoặc mật khẩu không chính xác!");
        }
        
        if (user.isLocked()) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa!");
        }
        
        return user;
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
        
        if (!user.getPassword().equals(hashPassword(oldPassword))) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác!");
        }
        
        user.setPassword(hashPassword(newPassword));
        userRepository.save(user);
    }

    public void toggleLock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
        user.setLocked(!user.isLocked());
        userRepository.save(user);
    }

    public void changeRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
        user.setRole(role);
        userRepository.save(user);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi mã hóa mật khẩu", e);
        }
    }
}
