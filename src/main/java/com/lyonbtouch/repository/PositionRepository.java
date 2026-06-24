package com.lyonbtouch.repository;

import com.lyonbtouch.model.Position;
import com.lyonbtouch.model.enums.PositionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByPositionCode(PositionCode positionCode);
}
