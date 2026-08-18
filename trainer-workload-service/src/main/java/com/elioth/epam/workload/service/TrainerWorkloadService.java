package com.elioth.epam.workload.service;

import com.elioth.epam.workload.domain.ActionType;
import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.dto.response.MonthSummaryResponse;
import com.elioth.epam.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.elioth.epam.workload.dto.response.YearSummaryResponse;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.elioth.epam.workload.persistence.MonthSummary;
import com.elioth.epam.workload.persistence.TrainerWorkloadDocument;
import com.elioth.epam.workload.persistence.YearSummary;
import com.elioth.epam.workload.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainerWorkloadService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadService.class);

    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadService(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    public void applyWorkload(TrainerWorkloadRequest request) {
        int year = request.trainingDate().getYear();
        int month = request.trainingDate().getMonthValue();

        LOG.info("operation=TRAINER_LOOKUP trainerUsername={}", request.trainerUsername());
        TrainerWorkloadDocument workload = repository.findByTrainerUsername(request.trainerUsername())
                .orElseGet(() -> {
                    if (request.actionType() == ActionType.DELETE) {
                        throw negativeDelete();
                    }
                    return createWorkload(request);
                });

        YearSummary yearSummary = request.actionType() == ActionType.ADD
                ? findOrCreateYear(workload, year)
                : findYear(workload, year);
        MonthSummary monthSummary = request.actionType() == ActionType.ADD
                ? findOrCreateMonth(yearSummary, month)
                : findMonth(yearSummary, month);
        long currentDuration = monthSummary.getTrainingsSummaryDuration();
        long newDuration = calculateDuration(currentDuration, request);

        updateTrainerProfile(workload, request);
        monthSummary.setTrainingsSummaryDuration(newDuration);
        LOG.info(
                "operation=WORKLOAD_UPDATE trainerUsername={} year={} month={} action={} previousDurationMinutes={} newDurationMinutes={}",
                request.trainerUsername(), year, month, request.actionType(), currentDuration, newDuration
        );

        repository.save(workload);
        LOG.info(
                "operation=WORKLOAD_PERSIST result=SUCCESS trainerUsername={} year={} month={}",
                request.trainerUsername(), year, month
        );
    }

    public TrainerWorkloadSummaryResponse getMonthlySummary(String username, int year, int month) {
        LOG.info("operation=TRAINER_LOOKUP trainerUsername={}", username);
        TrainerWorkloadDocument workload = repository.findByTrainerUsername(username)
                .orElseThrow(() -> notFound(username, year, month));

        YearSummary yearSummary = safeYears(workload).stream()
                .filter(candidate -> candidate.getYear() == year)
                .findFirst()
                .orElseThrow(() -> notFound(username, year, month));
        MonthSummary monthSummary = safeMonths(yearSummary).stream()
                .filter(candidate -> candidate.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> notFound(username, year, month));

        return new TrainerWorkloadSummaryResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                Boolean.TRUE.equals(workload.getTrainerStatus()),
                List.of(new YearSummaryResponse(
                        yearSummary.getYear(),
                        List.of(new MonthSummaryResponse(
                                monthSummary.getMonth(),
                                monthSummary.getTrainingsSummaryDuration()
                        ))
                ))
        );
    }

    private TrainerWorkloadDocument createWorkload(TrainerWorkloadRequest request) {
        LOG.info("operation=TRAINER_WORKLOAD_DOCUMENT result=CREATED trainerUsername={}", request.trainerUsername());
        TrainerWorkloadDocument workload = new TrainerWorkloadDocument();
        updateTrainerProfile(workload, request);
        return workload;
    }

    private YearSummary findOrCreateYear(TrainerWorkloadDocument workload, int year) {
        List<YearSummary> years = safeYears(workload);
        return years.stream()
                .filter(candidate -> candidate.getYear() == year)
                .findFirst()
                .map(existing -> {
                    LOG.info("operation=YEAR_LOOKUP result=FOUND year={}", year);
                    return existing;
                })
                .orElseGet(() -> {
                    YearSummary created = new YearSummary(year);
                    years.add(created);
                    LOG.info("operation=YEAR_LOOKUP result=CREATED year={}", year);
                    return created;
                });
    }

    private MonthSummary findOrCreateMonth(YearSummary yearSummary, int month) {
        List<MonthSummary> months = safeMonths(yearSummary);
        return months.stream()
                .filter(candidate -> candidate.getMonth() == month)
                .findFirst()
                .map(existing -> {
                    LOG.info("operation=MONTH_LOOKUP result=FOUND month={}", month);
                    return existing;
                })
                .orElseGet(() -> {
                    MonthSummary created = new MonthSummary(month, 0L);
                    months.add(created);
                    LOG.info("operation=MONTH_LOOKUP result=CREATED month={}", month);
                    return created;
                });
    }

    private YearSummary findYear(TrainerWorkloadDocument workload, int year) {
        return safeYears(workload).stream()
                .filter(candidate -> candidate.getYear() == year)
                .findFirst()
                .orElseThrow(this::negativeDelete);
    }

    private MonthSummary findMonth(YearSummary yearSummary, int month) {
        return safeMonths(yearSummary).stream()
                .filter(candidate -> candidate.getMonth() == month)
                .findFirst()
                .orElseThrow(this::negativeDelete);
    }

    private long calculateDuration(long currentDuration, TrainerWorkloadRequest request) {
        long newDuration = switch (request.actionType()) {
            case ADD -> Math.addExact(currentDuration, request.trainingDurationMinutes());
            case DELETE -> Math.subtractExact(currentDuration, request.trainingDurationMinutes());
        };
        if (newDuration < 0) {
            throw negativeDelete();
        }
        return newDuration;
    }

    private void updateTrainerProfile(TrainerWorkloadDocument workload, TrainerWorkloadRequest request) {
        workload.setTrainerUsername(request.trainerUsername());
        workload.setTrainerFirstName(request.trainerFirstName());
        workload.setTrainerLastName(request.trainerLastName());
        workload.setTrainerStatus(request.active());
    }

    private List<YearSummary> safeYears(TrainerWorkloadDocument workload) {
        if (workload.getYears() == null) {
            workload.setYears(new ArrayList<>());
        }
        return workload.getYears();
    }

    private List<MonthSummary> safeMonths(YearSummary yearSummary) {
        if (yearSummary.getMonths() == null) {
            yearSummary.setMonths(new ArrayList<>());
        }
        return yearSummary.getMonths();
    }

    private TrainerWorkloadNotFoundException notFound(String username, int year, int month) {
        return new TrainerWorkloadNotFoundException(
                "No workload found for trainer '%s' in %d-%02d".formatted(username, year, month)
        );
    }

    private InvalidWorkloadException negativeDelete() {
        return new InvalidWorkloadException("A DELETE operation cannot make the monthly workload negative");
    }
}
