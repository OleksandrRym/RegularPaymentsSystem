package org.orymar.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orymar.domain.EntriesPayment;
import org.orymar.domain.RegularPayment;
import org.orymar.repository.EntriesRepository;
import org.orymar.repository.RegularRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntriesService {

    private final EntriesRepository entriesRepository;
    private final RegularRepository regularRepository;
    private final char ACTIVE_STATUS = 'A';
    private final char STORNOVANA_STATUS = 'S';

    public EntriesPayment create(EntriesPayment entry) {
        validateStatus(entry.getStatus());
        return entriesRepository.save(entry);
    }

    public Optional<EntriesPayment> getById(UUID id) {
        return entriesRepository.findById(id);
    }

    public EntriesPayment update(UUID id, EntriesPayment updated) {
        EntriesPayment existingPayment = entriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "EntriesPayment not found with id: " + id));

        validateStatus(updated.getStatus());

        existingPayment.setAmount(updated.getAmount());
        existingPayment.setStatus(updated.getStatus());

        return entriesRepository.save(existingPayment);
    }

    private void validateStatus(char status) {
        if (status != ACTIVE_STATUS && status != STORNOVANA_STATUS) {
            throw new IllegalArgumentException(
                    "Invalid status: " + status + ". Allowed values: 'A' (Active) or 'S' (Stornovana)");
        }
    }

    public void delete(UUID id) {
        entriesRepository.deleteById(id);
    }

    public List<EntriesPayment> getEntriesByPaymentId(UUID paymentId) {
        return entriesRepository.findAllByRegularPaymentId(paymentId);
    }

    public EntriesPayment updateStatus(UUID id, char status) {
        EntriesPayment existingPayment = entriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "EntriesPayment not found with id: " + id));

        validateStatus(status);

        existingPayment.setStatus(status);
        return entriesRepository.save(existingPayment);
    }

    public boolean isWriteOffNeeded(UUID regularPaymentId) {
        RegularPayment regularPayment = regularRepository.findById(regularPaymentId).orElseThrow(() -> new IllegalArgumentException("Regular payment not found"));

        List<EntriesPayment> entries = entriesRepository.findAllByRegularPaymentId(regularPaymentId);

        if (entries.isEmpty()) {
            return true;
        }

        LocalDateTime lastPaymentDate = entries.stream().map(EntriesPayment::getDateOfPayment).max(Comparator.naturalOrder()).orElseThrow();

        LocalDateTime nextPaymentDate = lastPaymentDate.plus(regularPayment.getDebitPeriod());

        return LocalDateTime.now().isAfter(nextPaymentDate);
    }
}
