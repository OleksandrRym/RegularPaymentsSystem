package org.orymar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.orymar.domain.dto.CreateEntriesPaymentRequestDTO;
import org.orymar.domain.dto.OutputEntriesPaymentResponseDTO;
import org.orymar.domain.dto.UpdateEntriesPaymentRequestDTO;
import org.orymar.mapper.EntriesMapper;
import org.orymar.service.EntriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/entrie-payments")
@RequiredArgsConstructor
public class EntriesController {

    private final EntriesService service;
    private final EntriesMapper mapper;

    @GetMapping
    public ResponseEntity<List<OutputEntriesPaymentResponseDTO>> getByPaymentId(
            @RequestParam UUID paymentId) {
        var result = service.getEntriesByPaymentId(paymentId).stream().map(mapper::toEntriesPayment).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<OutputEntriesPaymentResponseDTO> create(
            @RequestBody @Valid CreateEntriesPaymentRequestDTO dto) {
        var entriesPayment = mapper.toEntriesPaymentCreateDto(dto);
        var created = service.create(entriesPayment);
        var entriesPaymentResponseDTO = mapper.toEntriesPayment(created);
        return new ResponseEntity<>(entriesPaymentResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutputEntriesPaymentResponseDTO> getById(@PathVariable UUID id) {
        return service
                .getById(id)
                .map(mapper::toEntriesPayment)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OutputEntriesPaymentResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateEntriesPaymentRequestDTO dto) {
        var entriesPayment = mapper.toEntriesPaymentUpdateDto(dto);
        var updated = service.update(id, entriesPayment);
        var entriesPaymentResponseDTO = mapper.toEntriesPayment(updated);
        return ResponseEntity.ok(entriesPaymentResponseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OutputEntriesPaymentResponseDTO> updateStatus(@PathVariable UUID id, @RequestParam char status) {
        var updated = service.updateStatus(id, status);
        var responseDTO = mapper.toEntriesPayment(updated);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/check")
    public boolean checkWriteOff(@RequestParam UUID id) {
        return service.isWriteOffNeeded(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}