package com.bookstore.controller;

import com.bookstore.dto.DashboardAnalyticsDTO;
import com.bookstore.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAnalyticsDTO> getDashboardAnalytics() {
        DashboardAnalyticsDTO analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
