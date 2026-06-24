package com.lyonbtouch.repository;

import com.lyonbtouch.model.Availability;
import com.lyonbtouch.model.Shift;
import com.lyonbtouch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByShift(Shift shift);

    List<Availability> findByUser(User user);

    Optional<Availability> findByUserAndShift(User user, Shift shift);
}
