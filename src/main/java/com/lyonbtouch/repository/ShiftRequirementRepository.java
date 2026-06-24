package com.lyonbtouch.repository;

import com.lyonbtouch.model.ShiftRequirement;
import com.lyonbtouch.model.Shift;
import com.lyonbtouch.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShiftRequirementRepository extends JpaRepository<ShiftRequirement, Long> {

    List<ShiftRequirement> findByShift(Shift shift);

    Optional<ShiftRequirement> findByShiftAndPosition(Shift shift, Position position);
}
