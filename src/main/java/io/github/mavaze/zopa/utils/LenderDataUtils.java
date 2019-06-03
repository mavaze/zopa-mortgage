package io.github.mavaze.zopa.utils;

import io.github.mavaze.zopa.dto.LenderDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

@Slf4j
@UtilityClass
public class LenderDataUtils {

    public static Map<BigDecimal, Long> groupingAmountsByRate(@NonNull final List<LenderDto> lenders) {

        if(lenders.isEmpty()) {
            log.warn("No lenders found in [market_file]. Returning empty ...");
            return emptyMap();
        }

        final Map<BigDecimal, Long> availableAmountPerRate = lenders.stream()
                .collect(toMap(
                        LenderDto::getRate,
                        lender -> lender.getAvailableAmount().longValue(),
                        LenderDataUtils::safeSumToLong,
                        TreeMap::new
                ));

        log.trace("Simplified market view of available amount and their corresponding rates: {}", availableAmountPerRate);
        return availableAmountPerRate;
    }

    private static Long safeSumToLong(@NonNull final Long a1, @NonNull final Long a2) {
        if (Long.MAX_VALUE - a1 >= a2) {
            return a1 + a2;
        }
        throw new RuntimeException("Too many lenders specified. Possible number overflow.");
    }
}
