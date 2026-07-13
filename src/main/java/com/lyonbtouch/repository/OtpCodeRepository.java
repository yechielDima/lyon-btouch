package com.lyonbtouch.repository;

import com.lyonbtouch.model.OtpCode;
import com.lyonbtouch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByUserAndUsedFalseOrderByCreatedAtDesc(User user);
}
