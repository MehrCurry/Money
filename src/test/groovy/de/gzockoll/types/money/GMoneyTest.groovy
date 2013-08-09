package de.gzockoll.types.money

import com.ibm.icu.util.ULocale

/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 09.08.13
 * Time: 08:25
 * To change this template use File | Settings | File Templates.
 */
class GMoneyTest extends GroovyTestCase {
        static EUR = com.ibm.icu.util.Currency.getInstance("EUR")
        static USD = com.ibm.icu.util.Currency.getInstance("USD")

        void setUp() {

        }

        void testFromMinor() {
            def m=Money.fromMinor(10,EUR)
            assert m.value == 10
        }

        void testFromMajor() {
            def m=Money.fromMajor(10,EUR)
            assert m.value == 1000
        }

        void testBigDecimal() {
            assert 0.01.getClass().getSimpleName() == "BigDecimal"
            assert 0.010.scale() == 3
        }

        void testPlus() {
            def m1=Money.fromMinor(10,EUR)
            def m2=Money.fromMinor(20,EUR)
            def m3=Money.fromMinor(30,EUR)
            assert m1 + m2 == m3
        }

        void testMinus() {
            def m1=Money.fromMinor(10,EUR)
            def m2=Money.fromMinor(20,EUR)
            def m3=Money.fromMinor(30,EUR)
            assert m3 - m2 == m1
        }

        void testWrongCurrency() {
            def m1=Money.fromMinor(10,EUR)
            def m2=Money.fromMinor(10,USD)

            shouldFail { m1 + m2 }
        }

        void testImmutable() {
            def m1=Money.fromMinor(10,EUR)

            shouldFail { m1.value=100 }
            shouldFail { m1.currency=USD }
        }

        void testSimpleAllocate() {
            def m1=Money.fromMinor(10,EUR)
            assert m1.allocate(3).collect { it.value} == [4,3,3]
        }

        void testRatioAllocate() {
            def m1=Money.fromMinor(20,EUR)
            assert m1.allocate([5,4,1]).collect { it.value} == [10,8,2]
        }
        /**
         * Tests multi allocation with remainder.
         */
        void testMultiAllocationWithRemainder() {
            def m = Money.fromMinor(6002, EUR);

            def alloc = m.allocate([3, 2, 1]);
            assert alloc.size() == 3
            assert alloc[0] == Money.fromMinor(3001, EUR)
            assert alloc[1] == Money.fromMinor(2001, EUR)
            assert alloc[2] == Money.fromMinor(1000, EUR)
        }
        /**
         * French Polynesia does not have subunit for their currency
         */

        void testCurrencyWithUnusualFraction_FrenchPolynesia() {
            com.ibm.icu.util.Currency cfpFranc = com.ibm.icu.util.Currency.getInstance(new ULocale.Builder()
                    .setRegion("PF").setLanguage("fr").build());
            assert cfpFranc.getDefaultFractionDigits() == 0
            def one = Money.fromMinor(1000, cfpFranc);
            assert one.value == 1000G
            assert Money.fromMajor(33, cfpFranc) == Money.fromMinor(33, cfpFranc)
        }

        /**
         * Tests compound interest calculation with Euro.
         */
        public void testCompountInterstEuro() {
            def m = Money.fromMinor(10000,EUR);
            def factor = 1.03G
            (0..<400).each { m = m.multiply(factor) }
            assert m.scaled() == Money.fromMinor(1364237182, EUR)
        }

        /**
         * Tests compound interest calculation with yen.
         */
        public void testCompountInterstJen() {
            def yen = com.ibm.icu.util.Currency.getInstance("JPY")
            def m = Money.fromMajor(100, yen)
            def factor = 1.03G
            (0..<400).each { m = m.multiply(factor) }
            assert m.scaled() == Money.fromMinor(13642372, yen)
        }
}
