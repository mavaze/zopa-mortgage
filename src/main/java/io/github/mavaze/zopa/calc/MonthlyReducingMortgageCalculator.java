package io.github.mavaze.zopa.calc;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.MathContext.DECIMAL32;

@Slf4j
@Component
@ConditionalOnProperty(name="zopa.mortgage.strategy", havingValue = "MONTHLY", matchIfMissing = true)
public class MonthlyReducingMortgageCalculator implements MortgageCalculator {

    @Value("${zopa.mortgage.loan-period-months:36}")
    private int loanPeriodInMonths;

    @Override
    public BigDecimal calculate(final Integer amount, @NonNull final BigDecimal annualRate) {

        log.info("Calculating repayment using monthly reducing method.");

        final BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 7, ROUND_HALF_UP);
        final BigDecimal principle = BigDecimal.valueOf(amount);
        final BigDecimal factor = monthlyRate.add(ONE).pow(loanPeriodInMonths, DECIMAL32);

        final BigDecimal repayment = principle.multiply(monthlyRate)
                .divide(factor.subtract(ONE), DECIMAL32)
                .multiply(factor);

        log.debug("Monthly repayment {} for a principle of {} with a compounding rate of {} over {} months",
                repayment, principle, monthlyRate, loanPeriodInMonths);

        return repayment;
    }
}
