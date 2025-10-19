package org.orymar.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.orymar.config.DebitPeriodDeserializer;

import java.math.BigDecimal;
import java.time.Duration;

public record UpdateRegularPaymentRequestDTO(
        @NotBlank(message = "PIB is required")
        @Size(min = 2, max = 100, message = "PIB length must be between 2 and 100 characters")
        String PIB,
        @NotBlank(message = "IPN is required")
        @Pattern(regexp = "\\d{9}", message = "IPN must be exactly 9 digits")
        String IPN,
        @NotBlank(message = "IBAN is required")
        @Pattern(regexp = "UA\\d{27}", message = "IBAN must start with 'UA' followed by 27 digits")
        String IBAN,
        @NotBlank(message = "MFO is required")
        @Pattern(regexp = "\\d{6}", message = "MFO must be exactly 6 digits")
        String MFO,
        @NotBlank(message = "EDRPOU is required")
        @Pattern(regexp = "\\d{8}", message = "EDRPOU must be exactly 8 digits")
        String EDRPOU,
        @NotBlank(message = "Beneficiary name is required")
        @Size(min = 2, max = 255, message = "Beneficiary name must be between 2 and 255 characters")
        String beneficiaryName,
        @NotNull(message = "Debit period is required")
        @Schema(example = "1m")
        @JsonDeserialize(using = DebitPeriodDeserializer.class)
        Duration debitPeriod,
        @NotNull(message = "Payment amount is required")
        @DecimalMin(
                value = "0.01",
                inclusive = true,
                message = "Payment amount must be greater than zero")
        @Digits(
                integer = 12,
                fraction = 2,
                message = "Payment amount must have max 12 digits and 2 decimal places")
        BigDecimal paymentAmount) {}
