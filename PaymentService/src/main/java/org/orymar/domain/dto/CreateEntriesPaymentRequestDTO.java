package org.orymar.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record CreateEntriesPaymentRequestDTO(
        @NotNull(message = "Payment ID is required")
        UUID regularPaymentId,

        @NotNull(message = "Payment date is required")
        LocalDateTime dateOfPayment,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @Digits(integer = 15, fraction = 2, message = "Amount must have max 15 digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Status is required")
        char status
) {}