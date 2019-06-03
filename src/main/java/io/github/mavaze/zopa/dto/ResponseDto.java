package io.github.mavaze.zopa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

@Data
@Builder
public class ResponseDto {

    @NonNull
    private PaymentDto<Integer> amount;

    @NonNull
    private BigDecimal rate;

    @NonNull
    private PaymentDto<BigDecimal> repaymentMonthly;

    @NonNull
    private PaymentDto<BigDecimal> repaymentTotal;

    @Override
    public String toString() {
        return String.format(
                "Requested amount: %s\n" +
                "Rate: %s%%\n" +
                "Monthly repayment: %s\n" +
                "Total repayment: %s",
                amount, rate, repaymentMonthly, repaymentTotal);
    }

    public static class ResponseDtoBuilder {

        public ResponseDtoBuilder amount(@NonNull final Integer amount) {
            this.amount = new PaymentDto<>(amount);
            return this;
        }

        public ResponseDtoBuilder rate(@NonNull final BigDecimal rate) {
            this.rate = rate.multiply(valueOf(100.0)).setScale(1, HALF_UP);
            return this;
        }

        public ResponseDtoBuilder repaymentMonthly(@NonNull final BigDecimal repaymentMonthly) {
            this.repaymentMonthly = new PaymentDto<>(repaymentMonthly.setScale(2, HALF_UP));
            return this;
        }

        public ResponseDtoBuilder repaymentTotal(@NonNull final BigDecimal repaymentTotal) {
            this.repaymentTotal = new PaymentDto<>(repaymentTotal.setScale(2, HALF_UP));
            return this;
        }
    }
}
