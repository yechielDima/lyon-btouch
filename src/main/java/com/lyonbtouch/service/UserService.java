package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.model.OtpCode;
import com.lyonbtouch.model.User;
import com.lyonbtouch.model.enums.AccountStatus;
import com.lyonbtouch.model.enums.SystemRole;
import com.lyonbtouch.repository.OtpCodeRepository;
import com.lyonbtouch.repository.UserRepository;
import com.lyonbtouch.sms.SmsSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final SmsSender smsSender;
    private final AuditService auditService;

    public UserService(UserRepository userRepository,
                       OtpCodeRepository otpCodeRepository,
                       SmsSender smsSender,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.otpCodeRepository = otpCodeRepository;
        this.smsSender = smsSender;
        this.auditService = auditService;
    }

    @Transactional
    public User register(String fullName, String phone) {
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new BusinessRuleException("Phone number already registered: " + phone);
        }

        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setSystemRole(SystemRole.EMPLOYEE);
        user.setAccountStatus(AccountStatus.PENDING);

        User saved = userRepository.save(user);
        auditService.log(null, "USER_REGISTER", "New user registered: " + fullName);

        List<User> managers = userRepository.findBySystemRole(SystemRole.MANAGER);
        for (User manager : managers) {
            smsSender.send(manager.getPhone(),
                    "New pending registration: " + fullName + " (" + phone + ")");
        }

        return saved;
    }

    @Transactional
    public void requestCode(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessRuleException("No account found for this phone number"));

        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            throw new BusinessRuleException("Account is pending approval");
        }

        if (!user.isActive()) {
            throw new BusinessRuleException("Account is deactivated");
        }

        String code = generateCode();

        OtpCode otp = new OtpCode();
        otp.setUser(user);
        otp.setCode(code);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpCodeRepository.save(otp);

        smsSender.send(phone, "Your Lyon B'Touch login code: " + code);
        auditService.log(user.getId(), "OTP_REQUESTED", "OTP requested for " + phone);
    }

    @Transactional
    public User verifyCode(String phone, String code) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessRuleException("No account found for this phone number"));

        OtpCode otp = otpCodeRepository.findTopByUserAndUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new BusinessRuleException("No active code found. Request a new one."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Code has expired. Request a new one.");
        }

        if (!otp.getCode().equals(code)) {
            throw new BusinessRuleException("Invalid code");
        }

        otp.setUsed(true);
        otpCodeRepository.save(otp);

        auditService.log(user.getId(), "OTP_VERIFIED", "Login via OTP for " + phone);
        user.getQualifications().size(); // Initialize lazy collection
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

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
