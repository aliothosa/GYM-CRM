package com.elioth.epam.workload.controller;

import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.elioth.epam.workload.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/workloads")
public class TrainerWorkloadController {

    private final TrainerWorkloadService workloadService;

    public TrainerWorkloadController(TrainerWorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public ResponseEntity<Void> updateWorkload(
            @Valid @RequestBody TrainerWorkloadRequest request
    ) {
        workloadService.applyWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadSummaryResponse> getMonthlyWorkload(
            @PathVariable @NotBlank String username,
            @RequestParam @Min(1900) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        return ResponseEntity.ok(
                workloadService.getMonthlySummary(username, year, month)
        );
    }
}