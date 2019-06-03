package io.github.mavaze.zopa.processor;

import io.github.mavaze.zopa.calc.MortgageCalculator;
import io.github.mavaze.zopa.dto.LenderDto;
import io.github.mavaze.zopa.dto.ResponseDto;
import io.github.mavaze.zopa.reader.LenderReader;
import io.github.mavaze.zopa.utils.LenderDataUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Component
@RequiredArgsConstructor
public class MortgageProcessor {

    @Value("${zopa.mortgage.loan-period-months:36}")
    private BigDecimal loanPeriodInMonths;

    private final LenderReader<File> lenderReader;

    private final MortgageCalculator mortgageCalculator;

    /**
     * Repayment can be calculated:
     * option 1: against available loan from individual lenders with their specific rates and sum them up
     * option 2: against whole requested amount using effective rate
     * @param args
     */
    public void process(@NonNull String... args) {

        log.info("Processing arguments '{}' to generate a quote", args);
        final List<LenderDto> lenders = lenderReader.parse(new File(args[0]));
        final Integer amount = Integer.parseInt(args[1]);

        final Map<BigDecimal, Long> filtered = filteredLoanAmountsByRate(lenders, amount);

        BigDecimal repayment = BigDecimal.ZERO;
        BigDecimal effectiveRate = BigDecimal.ZERO;

        for(Map.Entry<BigDecimal, Long> e : filtered.entrySet()) {
            final BigDecimal factor = rateProportionInOfferedLoan(amount, e.getKey(), e.getValue());
            effectiveRate = effectiveRate.add(factor);
            // option 1: calculate repayment against individual lenders with their specific rates and sum them up
            // repayment = repayment.add(
            //         mortgageCalculator.calculate(e.getValue().intValue(), e.getKey())
            // );
        }

        // option 2: calculate repayment against requested amount using effective rate
        repayment = mortgageCalculator.calculate(amount, effectiveRate);

        ResponseDto response = ResponseDto.builder()
                .amount(amount)
                .rate(effectiveRate)
                .repaymentMonthly(repayment)
                .repaymentTotal(repayment.multiply(loanPeriodInMonths))
                .build();

        // Instead of directly doing sysout here, we can pass it to some abstract console,
        // the one might employ sysout statements to print messages on screen.
        System.out.println(response);
    }

    /**
     * Filters the elements summing upto requested loan amount from different rates
     * ordered by rates with lowest rate appearing first.
     * @param lenders
     * @param requestedAmount
     * @return map of rate to available loan amounts
     */
    private Map<BigDecimal, Long> filteredLoanAmountsByRate(@NonNull final List<LenderDto> lenders,
                                                            @NonNull final Integer requestedAmount) {


        final Map<BigDecimal, Long> availableAmountPerRate = LenderDataUtils.groupingAmountsByRate(lenders);

        Long sum = 0L;
        final HashMap<BigDecimal, Long> filteredLoanAmountsByRate = new HashMap<>();
        for(Map.Entry<BigDecimal, Long> entry : availableAmountPerRate.entrySet()) {
            Long value = entry.getValue();
            sum += value;
            if(sum >= requestedAmount) {
                filteredLoanAmountsByRate.put(entry.getKey(), requestedAmount + value - sum);
                return filteredLoanAmountsByRate;
            }
            filteredLoanAmountsByRate.put(entry.getKey(), value);
        }
        throw new RuntimeException("It is not possible to provide a quote at this time.");
    }

    /**
     * Not sure of exact formula for effective rate, but definitely not average of rates from selected lenders.
     * Instead proportion of loan from individual lenders wrt the principle need to be taken into consideration.
     * @param amount requested
     * @param rate
     * @param availableLoan with given rate
     * @return
     */
    private BigDecimal rateProportionInOfferedLoan(@NonNull final Integer amount,
                                                   @NonNull final BigDecimal rate,
                                                   @NonNull final Long availableLoan) {
        return valueOf(availableLoan)
                .multiply(rate)
                .divide(valueOf(amount), HALF_UP);
    }
}
