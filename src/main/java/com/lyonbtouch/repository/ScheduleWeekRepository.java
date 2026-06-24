package com.lyonbtouch.repository;

import com.lyonbtouch.model.ScheduleWeek;
import com.lyonbtouch.model.enums.WeekStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleWeekRepository extends JpaRepository<ScheduleWeek, Long> {

    Optional<ScheduleWeek> findByWeekStartDate(LocalDate weekStartDate);

    List<ScheduleWeek> findByStatus(WeekStatus status);
}
