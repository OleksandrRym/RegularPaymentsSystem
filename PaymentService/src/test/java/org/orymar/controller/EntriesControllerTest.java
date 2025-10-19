package org.orymar.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orymar.domain.EntriesPayment;
import org.orymar.domain.dto.CreateEntriesPaymentRequestDTO;
import org.orymar.domain.dto.OutputEntriesPaymentResponseDTO;
import org.orymar.domain.dto.UpdateEntriesPaymentRequestDTO;
import org.orymar.mapper.EntriesMapper;
import org.orymar.service.EntriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntriesControllerTest {

    @Mock
    private EntriesService service;

    @Mock
    private EntriesMapper mapper;

    @InjectMocks
    private EntriesController controller;

    private UUID id;
    private UUID paymentId;
    private EntriesPayment payment;
    private OutputEntriesPaymentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        paymentId = UUID.randomUUID();

        payment = new EntriesPayment();
        payment.setId(id);
        payment.setRegularPaymentId(paymentId);
        payment.setDateOfPayment(LocalDateTime.now());
        payment.setAmount(BigDecimal.valueOf(1234.56));
        payment.setStatus('A');

        responseDTO = new OutputEntriesPaymentResponseDTO(
                id,
                paymentId,
                payment.getDateOfPayment(),
                payment.getAmount(),
                payment.getStatus()
        );
    }

    @Test
    @DisplayName("Create payment - success")
    void createSuccess() {
        CreateEntriesPaymentRequestDTO request = new CreateEntriesPaymentRequestDTO(
                paymentId,
                payment.getDateOfPayment(),
                payment.getAmount(),
                payment.getStatus()
        );

        when(mapper.toEntriesPaymentCreateDto(request)).thenReturn(payment);
        when(service.create(payment)).thenReturn(payment);
        when(mapper.toEntriesPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputEntriesPaymentResponseDTO> result = controller.create(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    @DisplayName("Get by ID - found")
    void getByIdFound() {
        when(service.getById(id)).thenReturn(Optional.of(payment));
        when(mapper.toEntriesPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputEntriesPaymentResponseDTO> result = controller.getById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    @DisplayName("Get by ID - not found")
    void getByIdNotFound() {
        when(service.getById(id)).thenReturn(Optional.empty());

        ResponseEntity<OutputEntriesPaymentResponseDTO> result = controller.getById(id);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    @DisplayName("Update payment - stornovana")
    void updateStornovana() {
        UpdateEntriesPaymentRequestDTO request = new UpdateEntriesPaymentRequestDTO(
                BigDecimal.valueOf(2000.00), 'S'
        );

        when(mapper.toEntriesPaymentUpdateDto(request)).thenReturn(payment);
        when(service.update(id, payment)).thenReturn(payment);
        when(mapper.toEntriesPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputEntriesPaymentResponseDTO> result = controller.update(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @ParameterizedTest
    @ValueSource(chars = {'A', 'S'})
    @DisplayName("Update status - valid status")
    void updateStatusSuccess(char status) {
        when(service.updateStatus(id, status)).thenReturn(payment);
        when(mapper.toEntriesPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputEntriesPaymentResponseDTO> result = controller.updateStatus(id, status);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    @DisplayName("Update status - service throws exception")
    void updateStatusThrowsException() {
        when(service.updateStatus(id, 'D')).thenThrow(new IllegalArgumentException("Invalid status: D"));

        assertThrows(IllegalArgumentException.class, () -> controller.updateStatus(id, 'D'));
    }

    @Test
    @DisplayName("Check write-off - true")
    void checkWriteOffTrue() {
        when(service.isWriteOffNeeded(id)).thenReturn(true);

        boolean result = controller.checkWriteOff(id);

        assertTrue(result);
    }

    @Test
    @DisplayName("Check write-off - false")
    void checkWriteOffFalse() {
        when(service.isWriteOffNeeded(id)).thenReturn(false);

        boolean result = controller.checkWriteOff(id);

        assertFalse(result);
    }

    @Test
    @DisplayName("Delete payment - success")
    void deleteSuccess() {
        doNothing().when(service).delete(id);

        ResponseEntity<Void> result = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(service, times(1)).delete(id);
    }

    @Test
    @DisplayName("Delete payment - throws exception")
    void deleteThrowsException() {
        doThrow(new RuntimeException("delete failed")).when(service).delete(id);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.delete(id));
        assertEquals("delete failed", ex.getMessage());
    }

    @Test
    @DisplayName("Get by payment ID - returns list")
    void getByPaymentId() {
        when(service.getEntriesByPaymentId(paymentId)).thenReturn(List.of(payment));
        when(mapper.toEntriesPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<List<OutputEntriesPaymentResponseDTO>> result = controller.getByPaymentId(paymentId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(responseDTO, result.getBody().get(0));
    }

    @Test
    @DisplayName("Get by payment ID - empty list")
    void getByPaymentIdEmpty() {
        when(service.getEntriesByPaymentId(paymentId)).thenReturn(List.of());

        ResponseEntity<List<OutputEntriesPaymentResponseDTO>> result = controller.getByPaymentId(paymentId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }
}