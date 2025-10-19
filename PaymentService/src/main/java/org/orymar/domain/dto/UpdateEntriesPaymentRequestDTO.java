package org.orymar.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateEntriesPaymentRequestDTO(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        @Digits(integer = 12, fraction = 2, message = "Amount must have max 12 digits and 2 decimal places")
        BigDecimal amount,
        @NotNull(message = "Status is required")
        char status
) {}