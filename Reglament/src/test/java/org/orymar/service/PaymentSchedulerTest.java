package org.orymar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orymar.client.PaymentClientHttp;
import org.orymar.domain.dto.CreateEntriesPaymentRequestDTO;
import org.orymar.domain.dto.OutputRegularPaymentResponseDTO;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentSchedulerTest {

    @Mock
    private PaymentClientHttp paymentClient;

    @InjectMocks
    private PaymentScheduler scheduler;

    private final UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "0,false", // no payments
            "1,true",  // one payment, should write off
            "1,false", // one payment, no write off
            "2,true"   // two payments, should write off
    })
    @DisplayName("processPayments should correctly handle various scenarios")
    void testProcessPayments(int paymentCount, boolean shouldWriteOff) {
        List<OutputRegularPaymentResponseDTO> payments = switch (paymentCount) {
            case 0 -> List.of();
            case 1 -> List.of(createPayment(id1));
            case 2 -> List.of(createPayment(id1), createPayment(id2));
            default -> throw new IllegalArgumentException("Unexpected count: " + paymentCount);
        };
        when(paymentClient.getAllPayments()).thenReturn(payments);
        when(paymentClient.isWriteOffNeeded(any(UUID.class))).thenReturn(shouldWriteOff);

        scheduler.processPayments();

        verify(paymentClient).getAllPayments();
        verify(paymentClient, times(paymentCount)).isWriteOffNeeded(any(UUID.class));
        if (shouldWriteOff && paymentCount > 0) {
            verify(paymentClient, times(paymentCount))
                    .createEntryPayment(any(CreateEntriesPaymentRequestDTO.class));
        } else {
            verify(paymentClient, never()).createEntryPayment(any());
        }
    }

    private OutputRegularPaymentResponseDTO createPayment(UUID id) {
        return new OutputRegularPaymentResponseDTO(
                id,
                "Rymar Oleksandr",
                "1234567890",
                "UA123456789012345678901234567",
                "123456",
                "12345678",
                "Rymar Oleks",
                Duration.ofDays(1),
                BigDecimal.valueOf(123.45)
        );
    }
}