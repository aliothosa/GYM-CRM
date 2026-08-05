package com.elioth.epam.workload.service;

import com.elioth.epam.workload.domain.ActionType;
import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.elioth.epam.workload.persistence.TrainerMonthlyWorkload;
import com.elioth.epam.workload.repository.TrainerMonthlyWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerMonthlyWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadService service;

    @Test
    void addCreatesMonthlyWorkloadUsingTheTrainingDatePeriod() {
        when(repository.findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                "john.doe", 2026, 8)).thenReturn(Optional.empty());

        service.applyWorkload(request(LocalDate.of(2026, 8, 5), 60L, ActionType.ADD));

        TrainerMonthlyWorkload saved = savedWorkload();
        assertEquals("john.doe", saved.getTrainerUsername());
        assertEquals(2026, saved.getWorkloadYear());
        assertEquals(8, saved.getWorkloadMonth());
        assertEquals(60L, saved.getTrainingDurationMinutes());
    }

    @Test
    void addAccumulatesDurationForTheSameTrainerAndMonth() {
        TrainerMonthlyWorkload existing = workload(2026, 8, 40L);
        when(repository.findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                "john.doe", 2026, 8)).thenReturn(Optional.of(existing));

        service.applyWorkload(request(LocalDate.of(2026, 8, 6), 20L, ActionType.ADD));

        assertEquals(60L, existing.getTrainingDurationMinutes());
        verify(repository).save(existing);
    }

    @Test
    void addUsesSeparateRecordsForDifferentMonthsAndYears() {
        when(repository.findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                eq("john.doe"), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.empty());

        service.applyWorkload(request(LocalDate.of(2026, 12, 31), 30L, ActionType.ADD));
        service.applyWorkload(request(LocalDate.of(2027, 1, 1), 45L, ActionType.ADD));

        ArgumentCaptor<TrainerMonthlyWorkload> captor = ArgumentCaptor.forClass(TrainerMonthlyWorkload.class);
        verify(repository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertEquals(12, captor.getAllValues().get(0).getWorkloadMonth());
        assertEquals(2026, captor.getAllValues().get(0).getWorkloadYear());
        assertEquals(1, captor.getAllValues().get(1).getWorkloadMonth());
        assertEquals(2027, captor.getAllValues().get(1).getWorkloadYear());
    }

    @Test
    void deleteSubtractsDurationWithoutChangingTheMonthlyPeriod() {
        TrainerMonthlyWorkload existing = workload(2026, 8, 60L);
        when(repository.findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                "john.doe", 2026, 8)).thenReturn(Optional.of(existing));

        service.applyWorkload(request(LocalDate.of(2026, 8, 8), 15L, ActionType.DELETE));

        assertEquals(45L, existing.getTrainingDurationMinutes());
        verify(repository).save(existing);
    }

    @Test
    void deleteRejectsAWorkloadThatWouldBecomeNegative() {
        TrainerMonthlyWorkload existing = workload(2026, 8, 10L);
        when(repository.findForUpdateByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                "john.doe", 2026, 8)).thenReturn(Optional.of(existing));

        assertThrows(InvalidWorkloadException.class,
                () -> service.applyWorkload(request(LocalDate.of(2026, 8, 8), 15L, ActionType.DELETE)));

        assertEquals(10L, existing.getTrainingDurationMinutes());
        verify(repository, never()).save(any());
    }

    @Test
    void returnsMonthlySummaryAndReportsMissingWorkload() {
        TrainerMonthlyWorkload existing = workload(2026, 8, 75L);
        when(repository.findByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                "john.doe", 2026, 8)).thenReturn(Optional.of(existing));

        var summary = service.getMonthlySummary("john.doe", 2026, 8);

        assertEquals("john.doe", summary.trainerUsername());
        assertEquals(75L, summary.years().getFirst().months().getFirst().trainingSummaryDurationMinutes());
        when(repository.findByTrainerUsernameAndWorkloadYearAndWorkloadMonth(
                "missing", 2026, 8)).thenReturn(Optional.empty());
        assertThrows(TrainerWorkloadNotFoundException.class,
                () -> service.getMonthlySummary("missing", 2026, 8));
    }

    private TrainerMonthlyWorkload savedWorkload() {
        ArgumentCaptor<TrainerMonthlyWorkload> captor = ArgumentCaptor.forClass(TrainerMonthlyWorkload.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    private TrainerMonthlyWorkload workload(int year, int month, long duration) {
        TrainerMonthlyWorkload workload = new TrainerMonthlyWorkload();
        workload.setTrainerUsername("john.doe");
        workload.setTrainerFirstName("John");
        workload.setTrainerLastName("Doe");
        workload.setTrainerActive(true);
        workload.setWorkloadYear(year);
        workload.setWorkloadMonth(month);
        workload.setTrainingDurationMinutes(duration);
        return workload;
    }

    private TrainerWorkloadRequest request(LocalDate date, long duration, ActionType action) {
        return new TrainerWorkloadRequest("john.doe", "John", "Doe", true, date, duration, action);
    }
}
