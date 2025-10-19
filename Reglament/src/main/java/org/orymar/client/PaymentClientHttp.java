package org.orymar.client;

import lombok.RequiredArgsConstructor;
import org.orymar.domain.dto.CreateEntriesPaymentRequestDTO;
import org.orymar.domain.dto.OutputEntriesPaymentResponseDTO;
import org.orymar.domain.dto.OutputRegularPaymentResponseDTO;
import org.orymar.exception.domin.PaymentServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentClientHttp {

    private final RestTemplate restTemplate;

    @Value("${payment-service.url}")
    private String host;

    private static final String REGULAR_BASE_URL = "/regular-payments";
    private static final String ENTRIES_BASE_URL = "/entrie-payments";
    private static final String WRITE_OFF_PATH = "/check?id={paymentId}";

    public List<OutputRegularPaymentResponseDTO> getAllPayments() {
        try {
            ResponseEntity<OutputRegularPaymentResponseDTO[]> response =
                    restTemplate.getForEntity(host + REGULAR_BASE_URL, OutputRegularPaymentResponseDTO[].class);
            return Arrays.asList(response.getBody());
        } catch (RestClientException e) {
            throw new PaymentServiceException("Failed to fetch regular payments", e);
        }
    }

    public boolean isWriteOffNeeded(UUID paymentId) {
        try {
            String url = host + ENTRIES_BASE_URL + WRITE_OFF_PATH;
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class, paymentId);
            return Boolean.TRUE.equals(response.getBody());
        } catch (RestClientException e) {
            throw new PaymentServiceException("Failed to check write-off for paymentId: " + paymentId, e);
        }
    }

    public OutputEntriesPaymentResponseDTO createEntryPayment(CreateEntriesPaymentRequestDTO dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CreateEntriesPaymentRequestDTO> request = new HttpEntity<>(dto, headers);

            ResponseEntity<OutputEntriesPaymentResponseDTO> response =
                    restTemplate.postForEntity(host + ENTRIES_BASE_URL, request, OutputEntriesPaymentResponseDTO.class);

            return response.getBody();
        } catch (RestClientException e) {
            throw new PaymentServiceException("Failed to create entry payment for regularPaymentId: " + dto.regularPaymentId(), e);
        }
    }
}