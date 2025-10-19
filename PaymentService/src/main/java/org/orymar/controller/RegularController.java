package org.orymar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.orymar.domain.dto.CreateRegularPaymentRequestDTO;
import org.orymar.domain.dto.OutputRegularPaymentResponseDTO;
import org.orymar.domain.dto.UpdateRegularPaymentRequestDTO;
import org.orymar.mapper.RegularMapper;
import org.orymar.service.RegularService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("regular-payments")
@RequiredArgsConstructor
public class RegularController {

    private final RegularService regularService;
    private final RegularMapper mapper;

    @PostMapping
    public ResponseEntity<OutputRegularPaymentResponseDTO> create(
            @RequestBody @Valid CreateRegularPaymentRequestDTO payment) {
        var regularPayment = mapper.toRegularPaymentCreateDto(payment);
        var created = regularService.create(regularPayment);
        var regularPaymentResponseDTO = mapper.toRegularPayment(created);
        return new ResponseEntity<>(regularPaymentResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutputRegularPaymentResponseDTO> getById(@PathVariable UUID id) {
        return regularService
                .getPaymentById(id)
                .map(mapper::toRegularPayment)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OutputRegularPaymentResponseDTO> update(
            @PathVariable UUID id, @RequestBody @Valid UpdateRegularPaymentRequestDTO payment) {
        var regularPayment = mapper.toRegularPaymentUpdateDto(payment);
        var updated = regularService.update(id, regularPayment);
        var regularPaymentResponseDTO = mapper.toRegularPayment(updated);
        return ResponseEntity.ok(regularPaymentResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        regularService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<OutputRegularPaymentResponseDTO>> getPayments(
            @RequestParam(required = false) String ipn,
            @RequestParam(required = false) String edrpou) {
        List<OutputRegularPaymentResponseDTO> payments;
        if (Objects.nonNull(ipn)) {
            payments = regularService.getPaymentsByIpn(ipn).stream()
                    .map(mapper::toRegularPayment)
                    .toList();
        } else if (Objects.nonNull(edrpou)) {
            payments = regularService.getPaymentsByErdpou(edrpou).stream()
                    .map(mapper::toRegularPayment)
                    .toList();
        } else {
            payments = regularService.getAllPayments().stream()
                    .map(mapper::toRegularPayment)
                    .toList();
        }
        return ResponseEntity.ok(payments);
    }
}
