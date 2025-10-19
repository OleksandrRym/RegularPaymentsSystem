package org.orymar.mapper;

import org.mapstruct.Mapper;
import org.orymar.domain.EntriesPayment;
import org.orymar.domain.dto.CreateEntriesPaymentRequestDTO;
import org.orymar.domain.dto.OutputEntriesPaymentResponseDTO;
import org.orymar.domain.dto.UpdateEntriesPaymentRequestDTO;

@Mapper(componentModel = "spring")
public interface EntriesMapper {
    EntriesPayment toEntriesPaymentCreateDto(CreateEntriesPaymentRequestDTO dto);

    EntriesPayment toEntriesPaymentUpdateDto(UpdateEntriesPaymentRequestDTO dto);

    OutputEntriesPaymentResponseDTO toEntriesPayment(EntriesPayment entriesPayment);
}
