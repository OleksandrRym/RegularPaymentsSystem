package org.orymar.domain.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

public record OutputRegularPaymentResponseDTO(
    UUID id,
    String PIB,
    String IPN,
    String IBAN,
    String MFO,
    String EDRPOU,
    String beneficiaryName,
    Duration debitPeriod,
    BigDecimal paymentAmount) {}
