package com.lyonbtouch.controller;

import com.lyonbtouch.dto.*;
import com.lyonbtouch.model.SwapRequest;
import com.lyonbtouch.model.enums.SwapStatus;
import com.lyonbtouch.service.SwapService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/swaps")
public class SwapController {

    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @PostMapping
    public ResponseEntity<SwapRequestResponse> openForSwap(@Valid @RequestBody SwapOpenRequest request) {
        SwapRequest swap = swapService.openForSwap(request.getEntryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toSwapResponse(swap));
    }

    @PutMapping("/{id}/offer")
    public ResponseEntity<SwapRequestResponse> offerToCover(@PathVariable Long id,
                                                             @Valid @RequestBody SwapOfferRequest request) {
        SwapRequest swap = swapService.offerToCover(id);
        return ResponseEntity.ok(DtoMapper.toSwapResponse(swap));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<SwapRequestResponse> approveSwap(@PathVariable Long id,
                                                            @Valid @RequestBody SwapActionRequest request) {
        SwapRequest swap = swapService.approveSwap(id);
        return ResponseEntity.ok(DtoMapper.toSwapResponse(swap));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<SwapRequestResponse> rejectSwap(@PathVariable Long id,
                                                           @Valid @RequestBody SwapActionRequest request) {
        SwapRequest swap = swapService.rejectSwap(id);
        return ResponseEntity.ok(DtoMapper.toSwapResponse(swap));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SwapRequestResponse> cancelSwap(@PathVariable Long id) {
        SwapRequest swap = swapService.cancelSwap(id);
        return ResponseEntity.ok(DtoMapper.toSwapResponse(swap));
    }

    @GetMapping
    public ResponseEntity<List<SwapRequestResponse>> getSwaps(@RequestParam(required = false) SwapStatus status,
                                                               @RequestParam(required = false) Long userId) {
        List<SwapRequest> swaps;
        if (status != null) {
            swaps = swapService.getSwapsByStatus(status);
        } else if (userId != null) {
            swaps = swapService.getSwapsForUser(userId);
        } else {
            swaps = swapService.getAllSwaps();
        }

        List<SwapRequestResponse> responses = swaps.stream()
                .map(DtoMapper::toSwapResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
