package com.lyonbtouch.repository;

import com.lyonbtouch.model.User;
import com.lyonbtouch.model.enums.AccountStatus;
import com.lyonbtouch.model.enums.SystemRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"qualifications"})
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"qualifications"})
    Optional<User> findByPhone(String phone);

    @EntityGraph(attributePaths = {"qualifications"})
    List<User> findByAccountStatus(AccountStatus accountStatus);

    @EntityGraph(attributePaths = {"qualifications"})
    List<User> findBySystemRole(SystemRole systemRole);

    @EntityGraph(attributePaths = {"qualifications"})
    List<User> findByActiveTrue();
}
