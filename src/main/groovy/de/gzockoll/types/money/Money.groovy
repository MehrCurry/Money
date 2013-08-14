package de.gzockoll.types.money
import com.ibm.icu.text.NumberFormat
import com.ibm.icu.util.Currency as Currency
import groovy.transform.EqualsAndHashCode
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
@EqualsAndHashCode
@ToString
class Money {
    static EUR=Currency.getInstance("EUR")
    BigDecimal value
    Currency currency

    static digits = [1,10,100,1000]

    static Money fromMinor(value,currency) {
        new Money(value/getCentFactor(currency),currency)
    }

    static Money fromMajor(value,Currency currency) {
        new Money(value ,currency)
    }

    Money() {
    }

    private Money(BigDecimal value, Currency currency) {
        this.value = value
        this.currency = currency
    }

    private void setValue(BigDecimal value) {
        this.value = value
    }

    private void setCurrency(Currency currency) {
        this.currency = currency
    }

    static getCentFactor(Currency currency) {
        assert null != currency
        digits[currency.getDefaultFractionDigits()]
    }
    def Money plus(Money other) {
        assertSameCurrency(other)
        new Money(value+other.value,currency)
    }

    def Money minus(Money other) {
        assertSameCurrency(other)
        new Money(value-other.value,currency)
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

    BigDecimal asMinor() {
        value*getCentFactor(currency)
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
        Money lowResult = Money.fromMinor((int)(asMinor() / n), currency);
        Money highResult =Money.fromMinor((int)(asMinor() / n + 1), currency);
        def results = [];
        def remainder = asMinor().longValue() % n;
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
        def remainder = asMinor()
        def results = []
        ratios.each {
            def part = ((asMinor() / total) * it).setScale(0,RoundingMode.DOWN)
            results << fromMinor(part, currency)
            remainder -= part
        }
        (0..<remainder).each {results[it] += one }
        results
    }

    /**
     * multiply by the given factor
     * @param factor
     * @return
     */
    def Money multiply(factor) {
        Money.fromMajor(value*factor,currency)
    }

    /**
     * rounds value to the correct scale after long calculations
     * @return
     */
    Money scaled() {
        fromMajor(value.setScale(currency.getDefaultFractionDigits(),RoundingMode.HALF_UP),currency)
    }

    /**
     *
     * @return the internal value as correct scaled BigDecimal (2 digits for cents etc.)
     */
    BigDecimal amount() {
        scaled().value
    }

    /**
     * Convenience Constructor
     *
     * @param i amount in euros
     * @return a now Money representing the amount
     */
    static Money euros(int i) {
        fromMajor(i,EUR)
    }

    /**
     * @return -this
     */
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
        return n.format(value.doubleValue());
    }

    static Money zero(currency) {
        Money.fromMinor(0,currency)
    }
}
