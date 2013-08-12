package de.gzockoll.types.money

import com.ibm.icu.text.NumberFormat
import com.ibm.icu.util.Currency as Currency
import groovy.transform.Immutable
import groovy.transform.ToString
import org.joda.time.DateTime
import org.joda.time.Interval

import java.math.RoundingMode

/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 08.08.13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
@Immutable(knownImmutableClasses = [Currency.class])
@ToString
class Money {
    static def EUR=Currency.getInstance("EUR")
    BigDecimal value=0G
    Currency currency

    static digits = [1,10,100,1000]

    static Money fromMinor(value,currency) {
        new Money(value: value,currency: currency)
    }

    static getCentFactor(Currency currency) {
        assert null != currency
        digits[currency.getDefaultFractionDigits()]
    }
    static Money fromMajor(value,Currency currency) {
        new Money(value: value*getCentFactor(currency) ,currency: currency)
    }

    def Money plus(Money other) {
        assertSameCurrency(other)
        new Money(value: value+other.value,currency: currency)
    }

    def Money minus(Money other) {
        assertSameCurrency(other)
        new Money(value: value-other.value,currency: currency)
    }

    def assertSameCurrency(Money other) {
        assert currency == other.currency
    }

    /**
     * @param currency
     * @throws IllegalArgumentException
     *             if currency is invalid
     */
    static assertCurrencyIsValid(Currency currency) {
        if (!isCurrencyValid(currency))
            throw new IllegalArgumentException("Invalid currency: " + currency)
    }

    /**
     * @param currency
     * @return true if currency is valid
     */
    static isCurrencyValid(Currency currency) {
        DateTime now = DateTime.now();
        return isCurrencyValid(currency, new Interval(now, now))
    }

    /**
     * @param currency
     * @param interval
     * @return true if currency is valid
     */
    static boolean isCurrencyValid(Currency currency, Interval interval) {
        return Currency.isAvailable(currency.getCurrencyCode(), interval.getStart().toDate(), interval.getEnd()
                .toDate())
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
        Money lowResult = Money.fromMinor((int)(value.intValue() / n), currency);
        Money highResult = new Money((int)(value.intValue() / n + 1), currency);
        def results = [];
        def remainder = value.intValue() % n;
        (0..<n).each { results << (it<remainder ? highResult : lowResult)}
        results;
    }

    /**
     * Allocate the amount according to ratios.
     *
     * @param ratios
     *            ratios.
     *
     * @return array with amount portions.
     */
    public Money[] allocate(ratios) {
        def one = fromMinor(1, currency)
        def total = ratios.collect{it}.sum(0)
        def remainder = value
        def results = []
        ratios.each {
            def part = ((value / total) * it).setScale(0,RoundingMode.DOWN)
            results << Money.fromMinor(part, currency)
            remainder -= part
        }
        (0..<remainder).each {results[it] += one }
        results
    }

    def Money multiply(factor) {
        Money.fromMinor(value*factor,currency)
    }

    Money scaled() {
        Money.fromMinor(value.setScale(0,RoundingMode.HALF_UP),currency)
    }

    BigDecimal getAmount() {
        (value / getCentFactor(currency)).setScale(currency.getDefaultFractionDigits(),RoundingMode.HALF_UP)
    }

    static Money euros(int i) {
        fromMajor(i,EUR)
    }

    long asMinor() {
        return value.longValue();
    }

    Money negate() {
        fromMinor(-value,currency)
    }

    /**
     * @param the
     *            Locale which is use for formatting
     * @return a string representing the amount and currency
     */
    String getAsFormattedString(Locale loc) {
        NumberFormat n = NumberFormat.getCurrencyInstance(loc);
        return n.format(amount.doubleValue());
    }
}