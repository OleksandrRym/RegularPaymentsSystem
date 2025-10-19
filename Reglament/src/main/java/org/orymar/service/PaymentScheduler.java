package org.orymar.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orymar.client.PaymentClientHttp;
import org.orymar.domain.dto.CreateEntriesPaymentRequestDTO;
import org.orymar.domain.dto.OutputRegularPaymentResponseDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentScheduler {
    private final PaymentClientHttp paymentClient;
    private final char ACTIVE_STATUS = 'A';

    @Scheduled(fixedRateString = "${scheduled.fixedRate}")
    public void processPayments() {
        log.info("Starting scheduled process for regular payments.");

        List<OutputRegularPaymentResponseDTO> payments = paymentClient.getAllPayments();

        for (OutputRegularPaymentResponseDTO payment : payments) {
            boolean shouldWriteOff = paymentClient.isWriteOffNeeded(payment.id());

            if (shouldWriteOff) {
                CreateEntriesPaymentRequestDTO dto =
                        new CreateEntriesPaymentRequestDTO(
                                payment.id(), LocalDateTime.now(), payment.paymentAmount(), ACTIVE_STATUS);
                paymentClient.createEntryPayment(dto);
                log.info("Entry created successfully for payment ID: {}", payment.id());
            }
        }

        log.info("Scheduled processing of regular payments completed.");
    }
}