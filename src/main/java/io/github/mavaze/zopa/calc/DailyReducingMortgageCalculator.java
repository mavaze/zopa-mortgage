package io.github.mavaze.zopa.calc;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.math.BigDecimal.*;
import static java.math.MathContext.DECIMAL32;

@Slf4j
@Component
@ConditionalOnProperty(name="zopa.mortgage.strategy", havingValue = "DAILY")
public class DailyReducingMortgageCalculator implements MortgageCalculator {

    @Value("${zopa.mortgage.loan-period-days:1080}")
    private int loanPeriodInDays;

    private static final int AVG_DAYS_PER_MONTH = 30;

    @Override
    public BigDecimal calculate(final Integer amount, @NonNull BigDecimal annualRate) {
        
        log.info("Calculating repayment using daily reducing method.");

        final BigDecimal dailyRate = annualRate.divide(valueOf(12 * AVG_DAYS_PER_MONTH), 7, ROUND_HALF_UP);
        final BigDecimal principle = valueOf(amount);
        final BigDecimal factor = dailyRate.add(ONE).pow(loanPeriodInDays, DECIMAL32);

        final BigDecimal repayment = principle.multiply(dailyRate)
                .divide(factor.subtract(ONE), DECIMAL32)
                .multiply(valueOf(AVG_DAYS_PER_MONTH))
                .multiply(factor);

        log.debug("Daily repayment {} for a principle of {} with a compounding rate of {} over {} days",
                repayment, principle, dailyRate, loanPeriodInDays);

        return repayment;
    }
}
