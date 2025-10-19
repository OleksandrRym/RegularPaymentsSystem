package org.orymar.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orymar.domain.RegularPayment;
import org.orymar.repository.RegularRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegularService {

    private final RegularRepository repository;

    public RegularPayment create(RegularPayment payment) {
        return repository.save(payment);
    }

    public Optional<RegularPayment> getPaymentById(UUID id) {
        return repository.findById(id);
    }

    public RegularPayment update(UUID id, RegularPayment updated) {
        Optional<RegularPayment> entriesPayment = repository.findById(id);
        if (entriesPayment.isEmpty()) {
            throw new EntityNotFoundException("RegularPayment not found with id: " + id);
        }
        var payment = entriesPayment.get();
        payment.setPIB(updated.getPIB());
        payment.setIPN(updated.getIPN());
        payment.setIBAN(updated.getIBAN());
        payment.setMFO(updated.getMFO());
        payment.setEDRPOU(updated.getEDRPOU());
        payment.setBeneficiaryName(updated.getBeneficiaryName());
        payment.setDebitPeriod(updated.getDebitPeriod());
        payment.setPaymentAmount(updated.getPaymentAmount());
        return repository.save(payment);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<RegularPayment> getPaymentsByIpn(String IPN) {
        return repository.findByIPN(IPN);
    }

    public List<RegularPayment> getAllPayments() {
        return repository.findAll();
    }

    public List<RegularPayment> getPaymentsByErdpou(String EDRPOU) {
        return repository.findByEDRPOU(EDRPOU);
    }
}
