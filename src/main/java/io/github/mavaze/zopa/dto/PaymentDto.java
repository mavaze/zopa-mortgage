package io.github.mavaze.zopa.dto;

import lombok.NonNull;

public class PaymentDto<T> {

    @NonNull
    private String currency;

    @NonNull
    private T amount;

    public PaymentDto(@NonNull String currency, @NonNull T amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public PaymentDto(@NonNull T amount) {
        this("£", amount);
    }

    @Override
    public String toString() {
        return currency + amount;
    }
}
