package org.orymar.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import lombok.Data;


@Data
@Entity
@Table(name = "regular_payment")
public class RegularPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "pib")
    private String PIB;

    @Column(name = "ipn")
    private String IPN;

    @Column(name = "iban")
    private String IBAN;

    @Column(name = "mfo")
    private String MFO;

    @Column(name = "edrpou")
    private String EDRPOU;

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "debit_period")
    private Duration debitPeriod;

    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;
}