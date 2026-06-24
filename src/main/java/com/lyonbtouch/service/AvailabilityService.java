package com.lyonbtouch.service;

import com.lyonbtouch.exception.BusinessRuleException;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.model.Availability;
import com.lyonbtouch.model.Shift;
import com.lyonbtouch.model.User;
import com.lyonbtouch.repository.AvailabilityRepository;
import com.lyonbtouch.repository.ShiftRepository;
import com.lyonbtouch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository,
                               UserRepository userRepository,
                               ShiftRepository shiftRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @Transactional
    public Availability submitAvailability(Long userId, Long shiftId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));

        if (availabilityRepository.findByUserAndShift(user, shift).isPresent()) {
            throw new BusinessRuleException("Availability already submitted for this shift");
        }

        Availability availability = new Availability();
        availability.setUser(user);
        availability.setShift(shift);
        return availabilityRepository.save(availability);
    }

    @Transactional
    public void removeAvailability(Long userId, Long shiftId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));

        Availability availability = availabilityRepository.findByUserAndShift(user, shift)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        availabilityRepository.delete(availability);
    }

    public List<Availability> getAvailabilityForShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return availabilityRepository.findByShift(shift);
    }

    public List<Availability> getAvailabilityForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return availabilityRepository.findByUser(user);
    }
}
