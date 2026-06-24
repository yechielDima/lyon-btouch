package com.lyonbtouch.repository;

import com.lyonbtouch.model.SwapRequest;
import com.lyonbtouch.model.ScheduleEntry;
import com.lyonbtouch.model.User;
import com.lyonbtouch.model.enums.SwapStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    List<SwapRequest> findByScheduleEntry(ScheduleEntry scheduleEntry);

    List<SwapRequest> findByRequestingUser(User requestingUser);

    List<SwapRequest> findByStatus(SwapStatus status);
}
