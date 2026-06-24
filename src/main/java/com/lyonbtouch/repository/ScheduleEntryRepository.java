package com.lyonbtouch.repository;

import com.lyonbtouch.model.ScheduleEntry;
import com.lyonbtouch.model.Shift;
import com.lyonbtouch.model.User;
import com.lyonbtouch.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {

    List<ScheduleEntry> findByShift(Shift shift);

    List<ScheduleEntry> findByUser(User user);

    Optional<ScheduleEntry> findByShiftAndUser(Shift shift, User user);

    List<ScheduleEntry> findByShiftAndPosition(Shift shift, Position position);
}
