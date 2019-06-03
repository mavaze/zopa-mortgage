package io.github.mavaze.zopa.calc;

import lombok.NonNull;

import java.math.BigDecimal;

public interface MortgageCalculator {

    BigDecimal calculate(@NonNull Integer amount, @NonNull BigDecimal annualRate);
}
