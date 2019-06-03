package io.github.mavaze.zopa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
public class LenderDto {

    @NonNull
    private final String name;

    @NonNull
    private final BigDecimal rate;

    @NonNull
    private final Integer availableAmount;

}
