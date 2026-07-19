package com.lyonbtouch.service;

import com.lyonbtouch.dto.AvailabilityResponse;
import com.lyonbtouch.dto.DtoMapper;
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
import java.util.stream.Collectors;

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
    public AvailabilityResponse submitAvailability(Long shiftId) {
        Long userId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));

        if (shift.getWeek().getStatus() != com.lyonbtouch.model.enums.WeekStatus.OPEN_FOR_SUBMISSION) {
            throw new BusinessRuleException("This week is not open for availability submission");
        }

        if (user.getSystemRole() == com.lyonbtouch.model.enums.SystemRole.SHIFT_MANAGER) {
            throw new BusinessRuleException("Shift managers cannot submit availability; they are assigned directly by the manager");
        }

        if (availabilityRepository.findByUserAndShift(user, shift).isPresent()) {
            throw new BusinessRuleException("Availability already submitted for this shift");
        }

        Availability availability = new Availability();
        availability.setUser(user);
        availability.setShift(shift);
        return DtoMapper.toAvailabilityResponse(availabilityRepository.save(availability));
    }

    @Transactional
    public void removeAvailability(Long shiftId) {
        Long userId = com.lyonbtouch.security.SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));

        if (shift.getWeek().getStatus() != com.lyonbtouch.model.enums.WeekStatus.OPEN_FOR_SUBMISSION) {
            throw new BusinessRuleException("This week is not open for availability submission");
        }

        Availability availability = availabilityRepository.findByUserAndShift(user, shift)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        availabilityRepository.delete(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilityForShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + shiftId));
        return availabilityRepository.findByShift(shift).stream()
                .map(DtoMapper::toAvailabilityResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilityForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return availabilityRepository.findByUser(user).stream()
                .map(DtoMapper::toAvailabilityResponse)
                .collect(Collectors.toList());
    }
}
