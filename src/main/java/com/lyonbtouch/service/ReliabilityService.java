package com.lyonbtouch.service;

import com.lyonbtouch.model.ReliabilityEvent;
import com.lyonbtouch.model.SwapRequest;
import com.lyonbtouch.model.User;
import com.lyonbtouch.exception.ResourceNotFoundException;
import com.lyonbtouch.repository.ReliabilityEventRepository;
import com.lyonbtouch.repository.SwapRequestRepository;
import com.lyonbtouch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReliabilityService {

    private final ReliabilityEventRepository reliabilityEventRepository;
    private final UserRepository userRepository;
    private final SwapRequestRepository swapRequestRepository;

    public ReliabilityService(ReliabilityEventRepository reliabilityEventRepository,
                              UserRepository userRepository,
                              SwapRequestRepository swapRequestRepository) {
        this.reliabilityEventRepository = reliabilityEventRepository;
        this.userRepository = userRepository;
        this.swapRequestRepository = swapRequestRepository;
    }

    @Transactional
    public ReliabilityEvent addEvent(Long userId, int pointsChange, String reason, Long swapRequestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ReliabilityEvent event = new ReliabilityEvent();
        event.setUser(user);
        event.setPointsChange(pointsChange);
        event.setReason(reason);

        if (swapRequestId != null) {
            SwapRequest swap = swapRequestRepository.findById(swapRequestId)
                    .orElseThrow(() -> new ResourceNotFoundException("SwapRequest not found: " + swapRequestId));
            event.setRelatedSwap(swap);
        }

        user.setReliabilityScore(user.getReliabilityScore() + pointsChange);
        userRepository.save(user);

        return reliabilityEventRepository.save(event);
    }

    public List<ReliabilityEvent> getEventsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return reliabilityEventRepository.findByUser(user);
    }
}
