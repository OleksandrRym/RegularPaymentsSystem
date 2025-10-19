package org.orymar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orymar.domain.RegularPayment;
import org.orymar.repository.RegularRepository;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegularServiceTest {

    @Mock
    private RegularRepository repository;

    @InjectMocks
    private RegularService regularService;

    private RegularPayment payment;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentId = UUID.randomUUID();

        payment = new RegularPayment();
        payment.setId(paymentId);
        payment.setPIB("Rymar Olksandr Andreevich");
        payment.setIPN("1234567890");
        payment.setIBAN("UA1234567891011121314151617181");
        payment.setMFO("123456");
        payment.setEDRPOU("12345678");
        payment.setBeneficiaryName("Oleks Fop");
        payment.setDebitPeriod(Duration.ofDays(1));
        payment.setPaymentAmount(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Update should update payment fields")
    void updatePaymentTest() {
        when(repository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(repository.save(payment)).thenReturn(payment);

        RegularPayment updated = new RegularPayment();
        updated.setPIB("Rymar Olksandr Andreevich");
        updated.setIPN("0123456789");
        updated.setIBAN("UA1234567891011121314151617181");
        updated.setMFO("123456");
        updated.setEDRPOU("12345678");
        updated.setBeneficiaryName("Oleks Fop");
        updated.setDebitPeriod(Duration.ofDays(2));
        updated.setPaymentAmount(BigDecimal.valueOf(2000));

        RegularPayment result = regularService.update(paymentId, updated);

        assertEquals("Rymar Olksandr Andreevich", result.getPIB());
        assertEquals("0123456789", result.getIPN());
        assertEquals("UA1234567891011121314151617181", result.getIBAN());
        assertEquals("123456", result.getMFO());
        assertEquals("12345678", result.getEDRPOU());
        assertEquals("Oleks Fop", result.getBeneficiaryName());
        assertEquals(Duration.ofDays(2), result.getDebitPeriod());
        assertEquals(BigDecimal.valueOf(2000), result.getPaymentAmount());
    }

    @Test
    @DisplayName("Should create regular payment")
    void createPaymentTest() {
        when(repository.save(payment)).thenReturn(payment);
        RegularPayment created = regularService.create(payment);
        assertEquals(payment, created);
        verify(repository).save(payment);
    }

    @Test
    @DisplayName("Should get payment by ID")
    void getPaymentByIdTest() {
        when(repository.findById(paymentId)).thenReturn(Optional.of(payment));
        Optional<RegularPayment> result = regularService.getPaymentById(paymentId);
        assertTrue(result.isPresent());
        assertEquals(payment, result.get());
    }

    @Test
    @DisplayName("Update should throw EntityNotFoundException if payment not found")
    void updateThrowsEntityNotFound() {
        when(repository.findById(paymentId)).thenReturn(Optional.empty());
        RegularPayment updated = new RegularPayment();
        assertThrows(EntityNotFoundException.class, () -> regularService.update(paymentId, updated));
    }


    @Test
    @DisplayName("Should delete payment")
    void deletePaymentTest() {
        doNothing().when(repository).deleteById(paymentId);
        regularService.delete(paymentId);
        verify(repository).deleteById(paymentId);
    }

    @Test
    @DisplayName("Should return payments by IPN")
    void getPaymentsByIpnTest() {
        List<RegularPayment> list = List.of(payment);
        when(repository.findByIPN(payment.getIPN())).thenReturn(list);

        List<RegularPayment> result = regularService.getPaymentsByIpn(payment.getIPN());
        assertEquals(list, result);
    }

    @Test
    @DisplayName("Should return payments by EDRPOU")
    void getPaymentsByErdpouTest() {
        List<RegularPayment> list = List.of(payment);
        when(repository.findByEDRPOU(payment.getEDRPOU())).thenReturn(list);

        List<RegularPayment> result = regularService.getPaymentsByErdpou(payment.getEDRPOU());
        assertEquals(list, result);
    }

    @Test
    @DisplayName("Should return all payments")
    void getAllPaymentsTest() {
        List<RegularPayment> list = List.of(payment);
        when(repository.findAll()).thenReturn(list);

        List<RegularPayment> result = regularService.getAllPayments();
        assertEquals(list, result);
    }
}