package com.lyonbtouch.repository;

import com.lyonbtouch.model.Shift;
import com.lyonbtouch.model.ScheduleWeek;
import com.lyonbtouch.model.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByWeek(ScheduleWeek week);

    Optional<Shift> findByShiftDateAndShiftType(LocalDate shiftDate, ShiftType shiftType);
}
