package de.gzockoll.types.money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class Money {

    private Currency unit;
    private BigDecimal amount;

    private Money(long amount, Currency currency) {
        this.unit = currency;
        this.amount = BigDecimal.valueOf(amount, currency.getDefaultFractionDigits());
    }

    /**
     * Create a new Money.
     * 
     * @param amount
     * @param unit
     */
    private Money(BigDecimal amount, Currency unit) {
        super();
        this.amount = amount;
        this.unit = unit;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getUnit() {
        return (Currency) unit;
    }

    public Money(long amount) {
        this(amount, getDefaultCurrency());
    }

    private static Currency getDefaultCurrency() {
        return getCurrencyByLocale(Locale.getDefault());
    }

    private static Currency getCurrencyByLocale(Locale l) {
        return Currency.getInstance(l);
    }

    public static Money fromMajor(long amount, Currency unit) {
        return new Money(amount * centFactor(unit), unit);
    }

    public static Money fromMinor(long amount, Currency unit) {
        return new Money(amount, unit);
    }

    private static final int[] cents = new int[] { 1, 10, 100, 1000 };

    private static int centFactor(Currency currency) {
        return cents[currency.getDefaultFractionDigits()];
    }

    public boolean equals(Object other) {
        if (other instanceof Money) {
            Validate.isTrue(amount.scale() == ((Money) other).amount.scale(), "Objects are not equaly scaled!");
            return EqualsBuilder.reflectionEquals(other, this);
        } else
            return false;
    }

    private void assertSameCurrencyAs(Money arg) {
        if (!unit.equals(arg.unit))
            throw new IllegalArgumentException("money math mismatch");
    }

    private Money newMoney(long amount) {
        Money money = new Money(amount, getUnit());
        return money;
    }

    public Money add(Money other) {
        assertSameCurrencyAs(other);
        return new Money(amount.add(other.amount), unit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public Money substract(Money other) {
        assertSameCurrencyAs(other);
        return new Money(amount.subtract(other.amount), unit);
    }

    public boolean greaterThan(Money other) {
        return (compareTo(other) > 0);
    }

    /**
     * @param other
     * @return
     */
    private int compareTo(Money other) {
        assertSameCurrencyAs(other);
        return amount.compareTo(other.amount);
    }

    public Money multiply(double factor) {
        MathContext ct = getMathContext();
        return multiply(new BigDecimal(factor, ct), ct.getRoundingMode());
    }

    /**
     * @return
     */
    private MathContext getMathContext() {
        return new MathContext(unit.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }

    public Money multiply(BigDecimal factor) {
        MathContext ct = getMathContext();
        return multiply(factor, ct.getRoundingMode());
    }

    public Money multiply(BigDecimal factor, RoundingMode roundingMode) {
        MathContext ct = getMathContext();
        BigDecimal value = factor.multiply(amount);
        return new Money(value, unit);
    }

    public Money scaled() {
        MathContext ct = getMathContext();
        return new Money(amount.setScale(ct.getPrecision(), ct.getRoundingMode()), unit);
    }

    public Money[] allocate(int n) {
        Money lowResult = Money.fromMinor(asMinor() / n, unit);
        Money highResult = new Money(asMinor() / n + 1, unit);
        Money[] results = new Money[n];
        int remainder = (int) asMinor() % n;
        for (int i = 0; i < remainder; i++)
            results[i] = highResult;
        for (int i = remainder; i < n; i++)
            results[i] = lowResult;
        return results;
    }

    public long asMinor() {
        return amount.multiply(new BigDecimal(centFactor())).longValue();
    }

    private Money newMoney(BigDecimal val) {
        return new Money(val, unit);
    }

    private long centFactor() {
        return centFactor(unit);
    }

    public Money[] allocate(long[] ratios) {
        long total = 0;
        for (int i = 0; i < ratios.length; i++)
            total += ratios[i];
        long remainder = asMinor();
        Money[] results = new Money[ratios.length];
        for (int i = 0; i < results.length; i++) {
            long part = asMinor() / total * ratios[i];
            results[i] = Money.fromMinor(part, unit);
            remainder -= part;
        }
        for (int i = 0; i < remainder; i++) {
            results[i] = results[i].add(fromMinor(1, unit));
        }
        return results;
    }

    public static Money cents(int i) {
        return fromMinor(i, Currency.getInstance("EUR"));
    }

    public static Money euros(long d) {
        return fromMajor(d, Currency.getInstance("EUR"));
    }

    public static Money zero() {
        return new Money(0, getDefaultCurrency());
    }

    public boolean isZero() {
        return equals(Money.zero());
    }

    /**
     * @return
     */
    public Money negate() {
        return new Money(amount.negate(), unit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
