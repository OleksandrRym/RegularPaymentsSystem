package org.orymar.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OutputEntriesPaymentResponseDTO(
        UUID id,
        UUID regularPaymentId,
        LocalDateTime dateOfPayment,
        BigDecimal amount,
        char status
) {}

