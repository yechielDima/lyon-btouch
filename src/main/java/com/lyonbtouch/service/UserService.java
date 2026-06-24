package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.model.User;
import com.lyonbtouch.model.enums.AccountStatus;
import com.lyonbtouch.model.enums.SystemRole;
import com.lyonbtouch.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional
    public User register(String fullName, String phone, String password) {
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new BusinessRuleException("Phone number already registered: " + phone);
        }

        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setSystemRole(SystemRole.EMPLOYEE);
        user.setAccountStatus(AccountStatus.PENDING);

        User saved = userRepository.save(user);
        auditService.log(null, "USER_REGISTER", "New user registered: " + fullName);
        return saved;
    }

    public User login(String phone, String password) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessRuleException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessRuleException("Invalid credentials");
        }

        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            throw new BusinessRuleException("Account is pending approval");
        }

        if (!user.isActive()) {
            throw new BusinessRuleException("Account is deactivated");
        }

        return user;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public List<User> getPendingUsers() {
        return userRepository.findByAccountStatus(AccountStatus.PENDING);
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findByActiveTrue();
    }
}
