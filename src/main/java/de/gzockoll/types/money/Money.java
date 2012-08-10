package de.gzockoll.types.money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.ibm.icu.util.Currency;

public class Money {

    /**
     * Creates an instance with i cents value and EUR as currency.
     * 
     * @param i
     *            cents
     */
    public static Money cents(int i) {
        return fromMinor(i, Currency.getInstance("EUR"));
    }

    /**
     * Creates an instance with major currency value amount.
     * 
     * @param amount
     *            major currency value
     * @param currency
     *            currency
     * 
     * @return Money instance
     */
    public static Money fromMajor(long amount, Currency currency) {
        return new Money(amount * centFactor(currency), currency);
    }

    /**
     * Creates an instance with minor currency value amount.
     * 
     * @param amount
     *            minor currency value
     * @param currency
     *            currency
     * 
     * @return Money instance
     */
    public static Money fromMinor(long amount, Currency currency) {
        return new Money(amount, currency);
    }

    /**
     * Returns the currency for the specified locale.
     * 
     * @param l
     *            locale
     * 
     * @return currency
     */
    private static Currency getCurrencyByLocale(Locale l) {
        return Currency.getInstance(l);
    }

    /**
     * Returns the default currency.
     * 
     * @return default currency.
     */
    private static Currency getDefaultCurrency() {
        return getCurrencyByLocale(Locale.getDefault());
    }

    /**
     * The currency.
     */
    private final Currency currency;

    /**
     * The amount.
     */
    private final BigDecimal amount;

    /**
     * Possible fraction values of cents.
     */
    private static final int[] cents = new int[] { 1, 10, 100, 1000 };

    /**
     * Creates a euro instance with major value d.
     * 
     * @param d
     *            euro amount
     * 
     * @return instance with value d euro.
     */
    public static Money euros(long d) {
        return fromMajor(d, Currency.getInstance("EUR"));
    }

    /**
     * Returns an instance with value 0 and default currency.
     * 
     * @return instance with value 0 and default currency.
     */
    public static Money zero() {
        return new Money(0, getDefaultCurrency());
    }

    /**
     * Returns the cent factor for the currency.
     * 
     * @param currency
     *            currency.
     * 
     * @return cent factor
     */
    private static int centFactor(Currency currency) {
        return cents[currency.getDefaultFractionDigits()];
    }

    /**
     * Create a new Money.
     * 
     * @param amount
     *            amount
     * @param currency
     *            currency
     */
    public Money(BigDecimal amount, Currency currency) {
        super();
        assertCurrencyIsValid(currency);
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * Create a new Money.
     * 
     * @param amount
     *            amount
     * @param currency
     *            currency
     */
    private Money(long amount, Currency currency) {
        this(BigDecimal.valueOf(amount, currency.getDefaultFractionDigits()), currency);
    }

    /**
     * @param currency
     * @throws IllegalArgumentException
     *             if currency is invalid
     */
    public void assertCurrencyIsValid(Currency currency) {
        if (!isCurrencyValid(currency))
            throw new IllegalArgumentException("Invalid currency: " + currency);
    }

    /**
     * @param currency
     * @return true if currency is valid
     */
    public static boolean isCurrencyValid(Currency currency) {
        DateTime now = new DateTime();
        return isCurrencyValid(currency, new Interval(now, now));
    }

    /**
     * @param currency
     * @param interval
     * @return true if currency is valid
     */
    public static boolean isCurrencyValid(Currency currency, Interval interval) {
        return Currency.isAvailable(currency.getCurrencyCode(), interval.getStart().toDate(), interval.getEnd()
                .toDate());
    }

    /**
     * Adds Money and returns result.
     * 
     * @param amount
     *            amount
     * @param currency
     *            currency
     * 
     * @return sum
     */
    public Money add(Money other) {
        assertSameCurrencyAs(other);
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Allocates the amount to n portion.
     * 
     * @param n
     *            number of portions.
     * 
     * @return array with amount portions.
     */
    public Money[] allocate(int n) {
        Money lowResult = Money.fromMinor(asMinor() / n, currency);
        Money highResult = new Money(asMinor() / n + 1, currency);
        Money[] results = new Money[n];
        int remainder = (int) asMinor() % n;
        for (int i = 0; i < remainder; i++)
            results[i] = highResult;
        for (int i = remainder; i < n; i++)
            results[i] = lowResult;
        return results;
    }

    /**
     * Allocate the amount according to ratios.
     * 
     * @param ratios
     *            ratios.
     * 
     * @return array with amount portions.
     */
    public Money[] allocate(long[] ratios) {
        long total = 0;
        for (int i = 0; i < ratios.length; i++)
            total += ratios[i];
        long remainder = asMinor();
        Money[] results = new Money[ratios.length];
        for (int i = 0; i < results.length; i++) {
            long part = asMinor() / total * ratios[i];
            results[i] = Money.fromMinor(part, currency);
            remainder -= part;
        }
        for (int i = 0; i < remainder; i++) {
            results[i] = results[i].add(fromMinor(1, currency));
        }
        return results;
    }

    /**
     * Returns the amount in minor currency.
     * 
     * @return amount in minor currency
     */
    public long asMinor() {
        return amount.multiply(new BigDecimal(centFactor())).longValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.getCurrencyCode().equals(other.currency.getCurrencyCode()))
            return false;
        return true;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param the
     *            Locale which is use for formatting
     * @return a string representing the amount and currency
     */
    public String getAsFormattedString(Locale loc) {
        NumberFormat n = NumberFormat.getCurrencyInstance(loc);
        return n.format(amount.doubleValue());
    }

    /**
     * Returns the currency.
     * 
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.getCurrencyCode().hashCode());
        return result;
    }

    /**
     * Determines if the amount is zero.
     * 
     * @return true if amount is zero.
     */
    public boolean isZero() {
        return equals(Money.zero());
    }

    /**
     * Multiplies this amount with a factor
     * 
     * @param factor
     * 
     * @return the result
     */
    public Money multiply(BigDecimal factor) {
        return new Money(factor.multiply(amount), currency);
    }

    /**
     * Multiplies this amount with a factor
     * 
     * @param factor
     * 
     * @return the result
     */
    public Money multiply(BigDecimal factor, MathContext ctx) {
        BigDecimal value = factor.multiply(amount, ctx);
        return new Money(value, currency);
    }

    /**
     * Multiplies this amount with a factor
     * 
     * @param factor
     * 
     * @return the result
     */
    public Money multiply(double factor) {
        MathContext ct = getMathContext();
        return multiply(new BigDecimal(factor, ct));
    }

    /**
     * Negates the amount
     * 
     * @return negated instance
     */
    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    /**
     * Scales the amount.
     * 
     * @return scaled instance
     */
    public Money scaled() {
        MathContext ct = getMathContext();
        return new Money(amount.setScale(ct.getPrecision(), ct.getRoundingMode()), currency);
    }

    /**
     * Subtracts an amount.
     * 
     * @param other
     *            amounut to subtract
     * 
     * @return result insance
     */
    public Money subtract(Money other) {
        assertSameCurrencyAs(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return amount + " " + currency.toString();
    }

    /**
     * Asserts that the passed Money has same currency.
     * 
     * @param arg
     *            money to test
     */
    private void assertSameCurrencyAs(Money arg) {
        if (!currency.equals(arg.currency))
            throw new IllegalArgumentException("money math mismatch");
    }

    /**
     * Returns the cent factor of this instance.
     * 
     * @return cent factor of this instance
     */
    private long centFactor() {
        return centFactor(currency);
    }

    private MathContext getMathContext() {
        return new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }
}
