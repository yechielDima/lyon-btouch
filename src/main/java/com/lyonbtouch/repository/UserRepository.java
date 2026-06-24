package com.lyonbtouch.repository;

import com.lyonbtouch.model.User;
import com.lyonbtouch.model.enums.AccountStatus;
import com.lyonbtouch.model.enums.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    List<User> findByAccountStatus(AccountStatus accountStatus);

    List<User> findBySystemRole(SystemRole systemRole);

    List<User> findByActiveTrue();
}
