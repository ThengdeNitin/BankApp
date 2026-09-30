package com.app.core_banking_service.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable Value Object representing monetary amount and currency.
 * Precision follows banking standard NUMERIC(18, 4) with HALF_EVEN rounding.
 */
public final class Money implements Comparable<Money>, Serializable {

    private static final long serialVersionUID = 1L;
    public static final int DEFAULT_SCALE = 4;
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
        if (currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
        this.amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        this.currency = currency.toUpperCase().trim();
    }

    public Money(BigDecimal amount, Currency currency) {
        this(amount, Objects.requireNonNull(currency, "Currency must not be null").getCurrencyCode());
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(long amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Multiplication factor must not be null");
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public Money multiply(long factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isPositiveOrZero() {
        return this.amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Compares this Money instance with another Money instance.
     * Enforces currency compatibility before comparing monetary values.
     *
     * @param other the Money instance to compare against
     * @return negative integer, zero, or positive integer as this amount is less than, equal to, or greater than other
     * @throws IllegalArgumentException if currencies do not match
     * @throws NullPointerException if other is null
     */
    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Cannot compare Money to null");
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money operand must not be null");
        if (!this.currency.equalsIgnoreCase(other.currency)) {
            throw new IllegalArgumentException(
                String.format("Currency mismatch: cannot operate between %s and %s", this.currency, other.currency)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equalsIgnoreCase(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency.toUpperCase());
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
