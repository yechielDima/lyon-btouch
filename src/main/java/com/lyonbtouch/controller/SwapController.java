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
        SwapRequestResponse swap = swapService.openForSwap(request.getEntryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(swap);
    }

    @PutMapping("/{id}/offer")
    public ResponseEntity<SwapRequestResponse> offerToCover(@PathVariable Long id,
                                                             @Valid @RequestBody SwapOfferRequest request) {
        SwapRequestResponse swap = swapService.offerToCover(id);
        return ResponseEntity.ok(swap);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<SwapRequestResponse> approveSwap(@PathVariable Long id,
                                                            @Valid @RequestBody SwapActionRequest request) {
        SwapRequestResponse swap = swapService.approveSwap(id);
        return ResponseEntity.ok(swap);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<SwapRequestResponse> rejectSwap(@PathVariable Long id,
                                                           @Valid @RequestBody SwapActionRequest request) {
        SwapRequestResponse swap = swapService.rejectSwap(id);
        return ResponseEntity.ok(swap);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SwapRequestResponse> cancelSwap(@PathVariable Long id) {
        SwapRequestResponse swap = swapService.cancelSwap(id);
        return ResponseEntity.ok(swap);
    }

    @GetMapping
    public ResponseEntity<List<SwapRequestResponse>> getSwaps(@RequestParam(required = false) SwapStatus status,
                                                               @RequestParam(required = false) Long userId) {
        List<SwapRequestResponse> swaps;
        if (status != null) {
            swaps = swapService.getSwapsByStatus(status);
        } else if (userId != null) {
            swaps = swapService.getSwapsForUser(userId);
        } else {
            swaps = swapService.getAllSwaps();
        }

        return ResponseEntity.ok(swaps);
    }
}
