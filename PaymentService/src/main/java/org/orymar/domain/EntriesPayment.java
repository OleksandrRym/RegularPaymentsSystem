package org.orymar.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "entries_payment")
public class EntriesPayment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "regular_payment_id")
  private UUID regularPaymentId;

  @Column(name = "date_of_payment")
  private LocalDateTime dateOfPayment;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "status")
  private char status;
}
