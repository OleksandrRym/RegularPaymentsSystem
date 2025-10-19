package org.orymar.repository;

import java.util.List;
import java.util.UUID;

import org.orymar.domain.RegularPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegularRepository extends JpaRepository<RegularPayment, UUID> {
    List<RegularPayment> findByIPN(String ipn);

    List<RegularPayment> findByEDRPOU(String edrpou);
}
