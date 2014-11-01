package de.gzockoll.types.money

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 09.08.13
 * Time: 08:25
 * To change this template use File | Settings | File Templates.
 */
class GMoneyTest {
    static EUR = Currency.getInstance("EUR")
    static USD = Currency.getInstance("USD")

    @Test
    void testFromMinor() {
        def m = Money.fromMinor(10, EUR)
        assert m.value == 0.10
    }

    @Test
    void testFromMajor() {
        def m = Money.fromMajor(10, EUR)
        assert m.value == 10
    }

    @Test
    void testBigDecimal() {
        assert 0.01.getClass().getSimpleName() == "BigDecimal"
        assert 0.010.scale() == 3
    }

    @Test
    void testPlus() {
        def m1 = Money.fromMinor(10, EUR)
        def m2 = Money.fromMinor(20, EUR)
        def m3 = Money.fromMinor(30, EUR)
        assert m1 + m2 == m3
    }

    @Test
    void testMinus() {
        def m1 = Money.fromMinor(10, EUR)
        def m2 = Money.fromMinor(20, EUR)
        def m3 = Money.fromMinor(30, EUR)
        assert m3 - m2 == m1
    }

    @Test
    void testDifferentCurrency() {
        def m1 = Money.fromMinor(10, EUR)
        def m2 = Money.fromMinor(10, USD)

        assert (m1 + m2) instanceof MoneyBag
    }

    @Test
    void xtestImmutable() {
        def m1 = Money.fromMinor(10, EUR)

        m1.value = 100
        // shouldFail { m1.value=100 }
        // shouldFail { m1.currency=USD }
    }

    @Test
    void testSimpleAllocate() {
        def m1 = Money.fromMinor(10, EUR)
        assert m1.allocate(3).collect { it.value } == [0.04, 0.03, 0.03]
    }

    @Test
    void testRatioAllocate() {
        def m1 = Money.fromMinor(20, EUR)
        assert m1.allocate([5, 4, 1]).collect { it.value } == [0.10, 0.08, 0.02]
    }

    /**
     * Tests multi allocation with remainder.
     */
    @Test
    void testMultiAllocationWithRemainder() {
        def m = Money.fromMinor(6002, EUR);

        def alloc = m.allocate([3, 2, 1]);
        assert alloc.size() == 3
        assert alloc.collect { it.value } == [30.01, 20.01, 10.00]
        assert alloc.collect { it.currency.currencyCode }.unique() == ["EUR"]
    }

    /**
     * French Polynesia does not have subunit for their currency (XPF)
     */
    @Test
    void testCurrencyWithUnusualFraction_FrenchPolynesia() {
        Currency cfpFranc = Currency.getInstance(new Locale("fr", "PF"));
        assert cfpFranc.getDefaultFractionDigits() == 0
        def one = Money.fromMinor(1000, "XPF");
        assert one.value == 1000G
        assert Money.fromMajor(33, "XPF") == Money.fromMinor(33, "XPF")
    }

    /**
     * Tests compound interest calculation with Euro.
     */
    @Test
    void testCompountInterstEuro() {
        def m = Money.fromMajor(10000, EUR);
        def factor = 1.03G
        (0..<400).each {
            m = m.multiply(factor)
        }
        assert m.scaled() == Money.fromMinor(136423718234, EUR)
    }

    /**
     * Tests compound interest calculation with yen.
     */
    @Test
    void testCompountInterstJen() {
        def m = Money.fromMajor(100, "JPY")
        def factor = 1.03G
        (0..<400).each { m = m.multiply(factor) }
        assert m.scaled() == Money.fromMinor(13642372, "JPY")
    }

    @Test
    void testSum() {
        def entries = []
        (1..10).each { entries << Money.euros(it) }
        assert entries.sum() == Money.euros(55)
    }

    @Test
    void testEuros() {
        def m = Money.euros(10)
        assert m.value == 10
        assert m.scaled().value == 10.00G
    }

    /*
    void testIsCurrencyValid() {
        def m = Money.fromMinor(10,"FRF")
        assert Money.isCurrencyValid(m.currency) == false
        assert Money.isCurrencyValid(m.currency,DateTime.parse("1991-06-25")) == true

        shouldFail { Money.assertCurrencyIsValid(m.currency)}
    }
    */
    @Test
    void testFormatedString() {
        def m = Money.fromMinor(1234, Currency.getInstance(Locale.US))
        assert m.getAsFormattedString(Locale.US) == '$12.34'
        assert m.getAsFormattedString(Locale.GERMAN) == 'USD 12,34'
    }

    @Test
    void testToString() {
        def m = Money.fromMinor(1234, Currency.getInstance(Locale.US))
        assert m.toString(Locale.GERMAN) == 'USD 12,34'

    }
}
