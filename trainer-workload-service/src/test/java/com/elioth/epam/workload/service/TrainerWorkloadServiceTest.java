package com.elioth.epam.workload.service;

import com.elioth.epam.workload.domain.ActionType;
import com.elioth.epam.workload.dto.request.TrainerWorkloadRequest;
import com.elioth.epam.workload.dto.response.TrainerWorkloadSummaryResponse;
import com.elioth.epam.workload.exception.InvalidWorkloadException;
import com.elioth.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.elioth.epam.workload.persistence.MonthSummary;
import com.elioth.epam.workload.persistence.TrainerWorkloadDocument;
import com.elioth.epam.workload.persistence.YearSummary;
import com.elioth.epam.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadService service;

    @Test
    void addCreatesTrainerYearMonthAndInitialDuration() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        service.applyWorkload(request(LocalDate.of(2026, 8, 5), 60L, ActionType.ADD));

        TrainerWorkloadDocument saved = savedDocument();
        assertEquals("john.doe", saved.getTrainerUsername());
        assertEquals("John", saved.getTrainerFirstName());
        assertEquals("Doe", saved.getTrainerLastName());
        assertEquals(Boolean.TRUE, saved.getTrainerStatus());
        assertEquals(1, saved.getYears().size());
        assertMonth(saved, 2026, 8, 60L);
    }

    @Test
    void addAccumulatesForExistingYearAndMonthAndUpdatesTrainerProfile() {
        TrainerWorkloadDocument existing = document(2026, 8, 40L);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existing));

        service.applyWorkload(new TrainerWorkloadRequest(
                "john.doe", "Jane", "Smith", false,
                LocalDate.of(2026, 8, 6), 20L, ActionType.ADD
        ));

        assertMonth(existing, 2026, 8, 60L);
        assertEquals("Jane", existing.getTrainerFirstName());
        assertEquals("Smith", existing.getTrainerLastName());
        assertEquals(Boolean.FALSE, existing.getTrainerStatus());
        verify(repository).save(existing);
    }

    @Test
    void addAppendsNewYearWithoutDuplicatingExistingYearOrMonth() {
        TrainerWorkloadDocument existing = document(2026, 8, 40L);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existing));

        service.applyWorkload(request(LocalDate.of(2027, 1, 1), 45L, ActionType.ADD));

        assertEquals(2, existing.getYears().size());
        assertMonth(existing, 2026, 8, 40L);
        assertMonth(existing, 2027, 1, 45L);
    }

    @Test
    void addAppendsNewMonthToExistingYearWithoutDuplicates() {
        TrainerWorkloadDocument existing = document(2026, 8, 40L);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existing));

        service.applyWorkload(request(LocalDate.of(2026, 9, 1), 45L, ActionType.ADD));
        service.applyWorkload(request(LocalDate.of(2026, 9, 2), 15L, ActionType.ADD));

        assertEquals(1, existing.getYears().size());
        assertEquals(2, existing.getYears().getFirst().getMonths().size());
        assertMonth(existing, 2026, 8, 40L);
        assertMonth(existing, 2026, 9, 60L);
    }

    @Test
    void deleteSubtractsAndRejectsNegativeDurationWithoutPersisting() {
        TrainerWorkloadDocument existing = document(2026, 8, 60L);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existing));

        service.applyWorkload(request(LocalDate.of(2026, 8, 8), 15L, ActionType.DELETE));
        assertMonth(existing, 2026, 8, 45L);
        clearInvocations(repository);

        assertThrows(InvalidWorkloadException.class,
                () -> service.applyWorkload(request(LocalDate.of(2026, 8, 8), 50L, ActionType.DELETE)));
        assertMonth(existing, 2026, 8, 45L);
        verify(repository, never()).save(any(TrainerWorkloadDocument.class));
    }

    @Test
    void deleteForMissingDocumentDoesNotCreateAWorkload() {
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(InvalidWorkloadException.class,
                () -> service.applyWorkload(request(LocalDate.of(2026, 8, 8), 15L, ActionType.DELETE)));

        verify(repository, never()).save(any(TrainerWorkloadDocument.class));
    }

    @Test
    void returnsMonthlySummaryAndReportsMissingDocumentOrMonth() {
        TrainerWorkloadDocument existing = document(2026, 8, 75L);
        when(repository.findByTrainerUsername("john.doe")).thenReturn(Optional.of(existing));

        TrainerWorkloadSummaryResponse summary = service.getMonthlySummary("john.doe", 2026, 8);

        assertEquals("john.doe", summary.trainerUsername());
        assertEquals(75L, summary.years().getFirst().months().getFirst().trainingSummaryDurationMinutes());
        assertThrows(TrainerWorkloadNotFoundException.class,
                () -> service.getMonthlySummary("john.doe", 2026, 9));
        when(repository.findByTrainerUsername("missing")).thenReturn(Optional.empty());
        assertThrows(TrainerWorkloadNotFoundException.class,
                () -> service.getMonthlySummary("missing", 2026, 8));
    }

    private TrainerWorkloadDocument savedDocument() {
        ArgumentCaptor<TrainerWorkloadDocument> captor = ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    private TrainerWorkloadDocument document(int year, int month, long duration) {
        TrainerWorkloadDocument document = new TrainerWorkloadDocument();
        document.setTrainerUsername("john.doe");
        document.setTrainerFirstName("John");
        document.setTrainerLastName("Doe");
        document.setTrainerStatus(true);
        YearSummary yearSummary = new YearSummary(year);
        yearSummary.setMonths(new java.util.ArrayList<>(List.of(new MonthSummary(month, duration))));
        document.setYears(new java.util.ArrayList<>(List.of(yearSummary)));
        return document;
    }

    private TrainerWorkloadRequest request(LocalDate date, long duration, ActionType action) {
        return new TrainerWorkloadRequest("john.doe", "John", "Doe", true, date, duration, action);
    }

    private void assertMonth(TrainerWorkloadDocument document, int year, int month, long duration) {
        YearSummary yearSummary = document.getYears().stream()
                .filter(candidate -> candidate.getYear() == year)
                .findFirst()
                .orElseThrow();
        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(candidate -> candidate.getMonth() == month)
                .findFirst()
                .orElseThrow();
        assertEquals(duration, monthSummary.getTrainingsSummaryDuration());
    }
}
