package com.elioth.epam.workload.service;

import com.elioth.epam.workload.dto.response.MonthSummaryResponse;
import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.elioth.epam.workload.dto.response.YearSummaryResponse;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.elioth.epam.workload.persistence.TrainerMonthlyWorkload;
import com.elioth.epam.workload.repository.TrainerMonthlyWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerWorkloadService {

    private static final Logger LOG =
            LoggerFactory.getLogger(TrainerWorkloadService.class);

    private final TrainerMonthlyWorkloadRepository repository;

    public TrainerWorkloadService(TrainerMonthlyWorkloadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void applyWorkload(TrainerWorkloadRequest request) {
        int year = request.trainingDate().getYear();
        int month = request.trainingDate().getMonthValue();

        LOG.info(
                "operation=APPLY_WORKLOAD trainerUsername={} action={} year={} month={} durationMinutes={}",
                request.trainerUsername(),
                request.actionType(),
                year,
                month,
                request.trainingDurationMinutes()
        );

        TrainerMonthlyWorkload workload = repository
                .findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(request.trainerUsername(), year, month)
                .orElseGet(() -> createEmptyWorkload(request, year, month));

        long currentDuration = workload.getTrainingDurationMinutes();
        long requestedDuration = request.trainingDurationMinutes();

        long newDuration = switch (request.actionType()) {
            case ADD -> Math.addExact(currentDuration, requestedDuration);
            case DELETE -> Math.subtractExact(currentDuration, requestedDuration);
        };

        if (newDuration < 0) {
            throw new InvalidWorkloadException(
                    "A DELETE operation cannot make the monthly workload negative"
            );
        }

        workload.setTrainerFirstName(request.trainerFirstName());
        workload.setTrainerLastName(request.trainerLastName());
        workload.setTrainerActive(request.active());
        workload.setTrainingDurationMinutes(newDuration);

        repository.save(workload);

        LOG.info(
                "operation=APPLY_WORKLOAD result=SUCCESS trainerUsername={} year={} month={} previousDurationMinutes={} newDurationMinutes={}",
                request.trainerUsername(),
                year,
                month,
                currentDuration,
                newDuration
        );
    }

    @Transactional(readOnly = true)
    public TrainerWorkloadSummaryResponse getMonthlySummary(
            String username,
            int year,
            int month
    ) {
        TrainerMonthlyWorkload workload = repository
                .findByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                        username,
                        year,
                        month
                )
                .orElseThrow(() -> new TrainerWorkloadNotFoundException(
                        "No workload found for trainer '%s' in %d-%02d"
                                .formatted(username, year, month)
                ));

        MonthSummaryResponse monthResponse = new MonthSummaryResponse(
                workload.getWorkloadMonth(),
                workload.getTrainingDurationMinutes()
        );

        YearSummaryResponse yearResponse = new YearSummaryResponse(
                workload.getWorkloadYear(),
                List.of(monthResponse)
        );

        return new TrainerWorkloadSummaryResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                workload.isTrainerActive(),
                List.of(yearResponse)
        );
    }

    private TrainerMonthlyWorkload createEmptyWorkload(
            TrainerWorkloadRequest request,
            int year,
            int month
    ) {
        TrainerMonthlyWorkload workload = new TrainerMonthlyWorkload();
        workload.setTrainerUsername(request.trainerUsername());
        workload.setTrainerFirstName(request.trainerFirstName());
        workload.setTrainerLastName(request.trainerLastName());
        workload.setTrainerActive(request.active());
        workload.setWorkloadYear(year);
        workload.setWorkloadMonth(month);
        workload.setTrainingDurationMinutes(0L);
        return workload;
    }
}