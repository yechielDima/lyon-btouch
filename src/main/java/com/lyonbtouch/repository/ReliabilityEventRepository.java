package com.lyonbtouch.repository;

import com.lyonbtouch.model.ReliabilityEvent;
import com.lyonbtouch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReliabilityEventRepository extends JpaRepository<ReliabilityEvent, Long> {

    List<ReliabilityEvent> findByUser(User user);
}
