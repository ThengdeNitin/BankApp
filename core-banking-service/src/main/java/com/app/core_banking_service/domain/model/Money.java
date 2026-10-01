package com.app.core_banking_service.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

import com.app.core_banking_service.domain.exception.CurrencyMismatchException;
import com.app.core_banking_service.domain.exception.InvalidTransactionAmountException;

public final class Money implements Comparable<Money> {

    private static final int FINANCIALSCALE = 4;

    public static final RoundingMode BANKING_ROUNDING = RoundingMode.HALF_EVEN;

    private final BigDecimal amount;

    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {

        if (amount == null) {
            throw new InvalidTransactionAmountException("Monetary amount cannot be null");
        }

        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }

        this.amount = amount.setScale(FINANCIALSCALE, BANKING_ROUNDING);

        this.currency = currency;

    }

    public static Money of(BigDecimal amount, String curreCode) {

        Currency parsedCurrency = Currency.getInstance(currencyCode);

        return new Money(amount, parsedCurrency);
    }

    public static Money of(String amountStr, String currencyCode) {
        return of(new BigDecimal(amountStr), currencyCode);
    }

    public static Money zero(String currencyCode) {
        return of(BigDecimal.ZERO, currencyCode);
    }

    public Money add(Money other) {
        assertSameCurrency(other);

        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);

        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        assertSameCurrency(other);

        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZeroOrNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    private void assertSameCurrentcy(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare or operate on null Money");
        }
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException("Currency mismatched : Opration attempted between "
                    + this.currency.getCurrencyCode() + " and "
                    + other.currency.getCurrencyCode());
        }
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public String getCurrencyCode() {
        return this.currency.getCurrencyCode();
    }

    @Override
    public int compareTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Money money = (Money) o;

        return this.amount.compareTo(money.amount) == 0 && Objects.equals(this.currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.amount.stripTrailingZeros(), this.currency);
    }

    @Override
    public String toString() {
        return this.currency.getCurrencyCode() + " " + this.amount.toPlainString();
    }

}
