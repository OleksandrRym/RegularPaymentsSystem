package org.orymar.repository;

import java.util.List;
import java.util.UUID;

import org.orymar.domain.EntriesPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntriesRepository extends JpaRepository<EntriesPayment, UUID> {
    List<EntriesPayment> findAllByRegularPaymentId(UUID id);
}
