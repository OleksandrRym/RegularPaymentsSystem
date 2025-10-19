package org.orymar.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orymar.domain.EntriesPayment;
import org.orymar.domain.RegularPayment;
import org.orymar.repository.EntriesRepository;
import org.orymar.repository.RegularRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntriesServiceTest {

    @Mock
    private EntriesRepository entriesRepository;

    @Mock
    private RegularRepository regularRepository;

    @InjectMocks
    private EntriesService entriesService;

    private EntriesPayment entry;
    private UUID entryId;
    private UUID regularPaymentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        entryId = UUID.randomUUID();
        regularPaymentId = UUID.randomUUID();

        entry = new EntriesPayment();
        entry.setId(entryId);
        entry.setRegularPaymentId(regularPaymentId);
        entry.setAmount(BigDecimal.valueOf(100));
        entry.setStatus('A');
        entry.setDateOfPayment(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("Should create entry")
    void createEntryTest() {
        when(entriesRepository.save(entry)).thenReturn(entry);
        EntriesPayment created = entriesService.create(entry);
        assertEquals(entry, created);
        verify(entriesRepository).save(entry);
    }

    @Test
    @DisplayName("Should get entry by ID")
    void getByIdTest() {
        when(entriesRepository.findById(entryId)).thenReturn(Optional.of(entry));
        Optional<EntriesPayment> result = entriesService.getById(entryId);
        assertTrue(result.isPresent());
        assertEquals(entry, result.get());
    }

    @Test
    @DisplayName("Update should throw EntityNotFoundException if entry not found")
    void updateThrowsEntityNotFound() {
        when(entriesRepository.findById(entryId)).thenReturn(Optional.empty());
        EntriesPayment updated = new EntriesPayment();
        updated.setAmount(BigDecimal.TEN);
        updated.setStatus('A');

        assertThrows(EntityNotFoundException.class, () -> entriesService.update(entryId, updated));
    }

    @ParameterizedTest
    @CsvSource({"A", "S"})
    @DisplayName("Update status with valid values should pass")
    void updateStatusValid(char status) {
        entry.setStatus('A');
        when(entriesRepository.findById(entryId)).thenReturn(Optional.of(entry));
        when(entriesRepository.save(entry)).thenReturn(entry);

        EntriesPayment updated = entriesService.updateStatus(entryId, status);

        assertEquals(status, updated.getStatus());
    }

    @ParameterizedTest
    @ValueSource(chars = {'X', 'B', ' '})
    @DisplayName("Update status with invalid values should throw IllegalArgumentException")
    void updateStatusInvalid(char status) {
        when(entriesRepository.findById(entryId)).thenReturn(Optional.of(entry));
        assertThrows(IllegalArgumentException.class, () -> entriesService.updateStatus(entryId, status));
    }

    @Test
    @DisplayName("Delete entry")
    void deleteEntryTest() {
        doNothing().when(entriesRepository).deleteById(entryId);
        entriesService.delete(entryId);
        verify(entriesRepository).deleteById(entryId);
    }

    @Test
    @DisplayName("Get entries by payment ID")
    void getEntriesByPaymentIdTest() {
        List<EntriesPayment> list = List.of(entry);
        when(entriesRepository.findAllByRegularPaymentId(regularPaymentId)).thenReturn(list);
        List<EntriesPayment> result = entriesService.getEntriesByPaymentId(regularPaymentId);
        assertEquals(list, result);
    }

    @Test
    @DisplayName("isWriteOffNeeded returns true if no entries")
    void isWriteOffNeededNoEntries() {
        RegularPayment regularPayment = new RegularPayment();
        regularPayment.setDebitPeriod(Duration.ofDays(1));
        when(regularRepository.findById(regularPaymentId)).thenReturn(Optional.of(regularPayment));
        when(entriesRepository.findAllByRegularPaymentId(regularPaymentId)).thenReturn(Collections.emptyList());

        assertTrue(entriesService.isWriteOffNeeded(regularPaymentId));
    }

    @Test
    @DisplayName("isWriteOffNeeded throws IllegalArgumentException if regular payment not found")
    void isWriteOffNeededThrowsIfNotFound() {
        when(regularRepository.findById(regularPaymentId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> entriesService.isWriteOffNeeded(regularPaymentId));
    }

    @Test
    @DisplayName("isWriteOffNeeded returns true if next payment date is passed")
    void isWriteOffNeededNextDatePassed() {
        RegularPayment regularPayment = new RegularPayment();
        regularPayment.setDebitPeriod(Duration.ofDays(1));
        LocalDateTime lastDate = LocalDateTime.now().minusDays(2);
        entry.setDateOfPayment(lastDate);

        when(regularRepository.findById(regularPaymentId)).thenReturn(Optional.of(regularPayment));
        when(entriesRepository.findAllByRegularPaymentId(regularPaymentId)).thenReturn(List.of(entry));

        assertTrue(entriesService.isWriteOffNeeded(regularPaymentId));
    }
    @Test
    @DisplayName("Update should correctly change amount and status")
    void updateChangesAmountAndStatus() {
        EntriesPayment updated = new EntriesPayment();
        updated.setAmount(BigDecimal.valueOf(500));
        updated.setStatus('S');

        when(entriesRepository.findById(entryId)).thenReturn(Optional.of(entry));
        when(entriesRepository.save(entry)).thenReturn(entry);

        EntriesPayment result = entriesService.update(entryId, updated);

        assertEquals(BigDecimal.valueOf(500), result.getAmount());
        assertEquals('S', result.getStatus());
        verify(entriesRepository).save(entry);
    }

    @Test
    @DisplayName("validateStatus should throw exception for invalid status")
    void validateStatusThrowsForInvalid() {
        char invalidStatus = 'X';
        when(entriesRepository.findById(entryId)).thenReturn(Optional.of(entry));

        assertThrows(IllegalArgumentException.class, () -> entriesService.updateStatus(entryId, invalidStatus));
    }
    @Test
    @DisplayName("isWriteOffNeeded returns false if next payment date not passed")
    void isWriteOffNeededNextDateNotPassed() {
        RegularPayment regularPayment = new RegularPayment();
        regularPayment.setDebitPeriod(Duration.ofDays(2));

        LocalDateTime lastDate = LocalDateTime.now().minusDays(1);
        entry.setDateOfPayment(lastDate);

        when(regularRepository.findById(regularPaymentId)).thenReturn(Optional.of(regularPayment));
        when(entriesRepository.findAllByRegularPaymentId(regularPaymentId)).thenReturn(List.of(entry));

        assertFalse(entriesService.isWriteOffNeeded(regularPaymentId));
    }
}