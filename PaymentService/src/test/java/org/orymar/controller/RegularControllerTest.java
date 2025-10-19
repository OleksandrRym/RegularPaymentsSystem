package org.orymar.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orymar.domain.RegularPayment;
import org.orymar.domain.dto.CreateRegularPaymentRequestDTO;
import org.orymar.domain.dto.OutputRegularPaymentResponseDTO;
import org.orymar.domain.dto.UpdateRegularPaymentRequestDTO;
import org.orymar.mapper.RegularMapper;
import org.orymar.service.RegularService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegularControllerTest {

    @Mock
    private RegularService regularService;

    @Mock
    private RegularMapper mapper;

    @InjectMocks
    private RegularController controller;

    private UUID id;
    private RegularPayment payment;
    private OutputRegularPaymentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();

        payment = new RegularPayment();
        payment.setId(id);
        payment.setPIB("Rymar Oleksandr");
        payment.setIPN("123456789");
        payment.setIBAN("UA123456789012345678901234567");
        payment.setMFO("123456");
        payment.setEDRPOU("12345678");
        payment.setBeneficiaryName("Rymar Oleksandr");
        payment.setDebitPeriod(Duration.ofDays(1));
        payment.setPaymentAmount(BigDecimal.valueOf(100));

        responseDTO = new OutputRegularPaymentResponseDTO(
                id,
                "Rymar Oleksandr",
                "123456789",
                "UA123456789012345678901234567",
                "123456",
                "12345678",
                "Rymar Oleks",
                Duration.ofDays(1),
                BigDecimal.valueOf(100)
        );
    }

    @Test
    @DisplayName("Update payment - service throws exception")
    void updateThrowsException() {
        UpdateRegularPaymentRequestDTO request = mock(UpdateRegularPaymentRequestDTO.class);
        when(mapper.toRegularPaymentUpdateDto(request)).thenReturn(payment);
        when(regularService.update(id, payment)).thenThrow(new RuntimeException("update failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.update(id, request));
        assertEquals("update failed", ex.getMessage());
    }

    @Test
    @DisplayName("Delete payment - service throws exception")
    void deleteThrowsException() {
        doThrow(new RuntimeException("delete failed")).when(regularService).delete(id);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.delete(id));
        assertEquals("delete failed", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "123456789, , ipn",
            ", 12345678, edrpou",
            ", , all",
            "999999999, , ipn-empty",
            ", 00000000, edrpou-empty"
    })
    @DisplayName("Get payments - handle empty lists")
    void getPaymentsHandlesEmpty(String ipn, String edrpou, String type) {
        if ("ipn".equals(type)) {
            when(regularService.getPaymentsByIpn(ipn)).thenReturn(List.of());
        } else if ("edrpou".equals(type)) {
            when(regularService.getPaymentsByErdpou(edrpou)).thenReturn(List.of());
        } else if ("all".equals(type)) {
            when(regularService.getAllPayments()).thenReturn(List.of(payment));
            when(mapper.toRegularPayment(payment)).thenReturn(responseDTO);
        } else {
            when(regularService.getPaymentsByIpn(ipn)).thenReturn(List.of());
            when(regularService.getPaymentsByErdpou(edrpou)).thenReturn(List.of());
        }

        ResponseEntity<List<OutputRegularPaymentResponseDTO>> result = controller.getPayments(ipn, edrpou);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        if ("all".equals(type)) {
            assertEquals(1, result.getBody().size());
        } else {
            assertTrue(result.getBody().isEmpty());
        }
    }

    @Test
    @DisplayName("GetPayments - mapper returns null")
    void getPaymentsMapperReturnsNull() {
        when(regularService.getAllPayments()).thenReturn(List.of(payment));
        when(mapper.toRegularPayment(payment)).thenReturn(null);

        ResponseEntity<List<OutputRegularPaymentResponseDTO>> result = controller.getPayments(null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertNull(result.getBody().get(0));
    }

    @Test
    @DisplayName("Test create payment")
    void testCreate() {
        CreateRegularPaymentRequestDTO request = new CreateRegularPaymentRequestDTO(
                "Rymar Oleks", "123456789", "UA123456789012345678901234567",
                "123456", "12345678", "Rymar Oleks", Duration.ofDays(1), BigDecimal.valueOf(100)
        );

        when(mapper.toRegularPaymentCreateDto(request)).thenReturn(payment);
        when(regularService.create(payment)).thenReturn(payment);
        when(mapper.toRegularPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputRegularPaymentResponseDTO> result = controller.create(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    @DisplayName("Test getById - found")
    void testGetByIdFound() {
        when(regularService.getPaymentById(id)).thenReturn(Optional.of(payment));
        when(mapper.toRegularPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputRegularPaymentResponseDTO> result = controller.getById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    @DisplayName("Test getById - not found")
    void testGetByIdNotFound() {
        when(regularService.getPaymentById(id)).thenReturn(Optional.empty());

        ResponseEntity<OutputRegularPaymentResponseDTO> result = controller.getById(id);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    @DisplayName("Test update payment")
    void testUpdate() {
        UpdateRegularPaymentRequestDTO request = new UpdateRegularPaymentRequestDTO(
                "Rymar Oleksandr", "123456789", "UA123456789012345678901234567",
                "123456", "12345678", "Rymar Oleksandr", Duration.ofDays(1), BigDecimal.valueOf(100)
        );

        when(mapper.toRegularPaymentUpdateDto(request)).thenReturn(payment);
        when(regularService.update(id, payment)).thenReturn(payment);
        when(mapper.toRegularPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<OutputRegularPaymentResponseDTO> result = controller.update(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseDTO, result.getBody());
    }

    @Test
    @DisplayName("Test delete payment")
    void testDelete() {
        doNothing().when(regularService).delete(id);

        ResponseEntity<Void> result = controller.delete(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(regularService, times(1)).delete(id);
    }

    @ParameterizedTest
    @CsvSource({
            "123456789, , ipn",
            ", 12345678, edrpou",
            ", , all"
    })
    @DisplayName("Test getPayments with ipn, edrpou and all")
    void testGetPayments(String ipn, String edrpou, String type) {
        if ("ipn".equals(type)) {
            when(regularService.getPaymentsByIpn(ipn)).thenReturn(List.of(payment));
        } else if ("edrpou".equals(type)) {
            when(regularService.getPaymentsByErdpou(edrpou)).thenReturn(List.of(payment));
        } else {
            when(regularService.getAllPayments()).thenReturn(List.of(payment));
        }
        when(mapper.toRegularPayment(payment)).thenReturn(responseDTO);

        ResponseEntity<List<OutputRegularPaymentResponseDTO>> result = controller.getPayments(ipn, edrpou);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(responseDTO, result.getBody().get(0));
    }
}