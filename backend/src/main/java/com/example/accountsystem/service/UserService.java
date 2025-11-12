package com.example.accountsystem.service;

import com.example.accountsystem.dto.LoginRequest;
import com.example.accountsystem.dto.RegisterRequest;
import com.example.accountsystem.dto.UserResponse;
import com.example.accountsystem.exception.BadRequestException;
import com.example.accountsystem.exception.ResourceNotFoundException;
import com.example.accountsystem.model.User;
import com.example.accountsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 檢查 Email 是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("此 Email 已被註冊");
        }

        // 建立新用戶
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 實際應用中應該加密密碼
        user.setBalance(BigDecimal.ZERO);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getBalance(),
                savedUser.getCreatedAt()
        );
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email 或密碼錯誤"));

        // 實際應用中應該驗證加密的密碼
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Email 或密碼錯誤");
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBalance(),
                user.getCreatedAt()
        );
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用戶不存在"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用戶不存在"));
    }

    @Transactional
    public void updateBalance(Long userId, BigDecimal newBalance) {
        User user = findById(userId);
        user.setBalance(newBalance);
        userRepository.save(user);
    }
}
