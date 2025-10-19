package org.orymar.mapper;

import org.mapstruct.Mapper;
import org.orymar.domain.RegularPayment;
import org.orymar.domain.dto.CreateRegularPaymentRequestDTO;
import org.orymar.domain.dto.OutputRegularPaymentResponseDTO;
import org.orymar.domain.dto.UpdateRegularPaymentRequestDTO;

@Mapper(componentModel = "spring")
public interface RegularMapper {
    RegularPayment toRegularPaymentCreateDto(CreateRegularPaymentRequestDTO dto);

    RegularPayment toRegularPaymentUpdateDto(UpdateRegularPaymentRequestDTO dto);

    OutputRegularPaymentResponseDTO toRegularPayment(RegularPayment regularPayment);
}
