package de.gzockoll.types.money
import groovy.transform.ToString
/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 16.08.13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
@ToString
class MoneyBagTest extends GroovyTestCase {

    void testMoneyBag() {
        def usd=Money.fromMajor(10,"USD")
        def eur=Money.fromMajor(11,"EUR")

        MoneyBag bag=usd + eur
        assert bag.getByCurrency(usd.currency) == Money.fromMajor(10,"USD")
        assert bag.getByCurrency(eur.currency) == Money.fromMajor(11,"EUR")
    }

    void testMoneyBagSameCurrency() {
        def e1=Money.fromMajor(10,"EUR")
        def e2=Money.fromMajor(11,"EUR")

        IMoney m=MoneyBag.create(e1,e2)
        assert m == Money.euros(21)
    }

    void testMultiply() {
        def usd=Money.fromMajor(10,"USD")
        def eur=Money.fromMajor(11,"EUR")

        MoneyBag bag=usd + eur
        bag=bag.multiply(9)
        assert bag.entries().values().containsAll([Money.fromMajor(99,"EUR"),Money.fromMajor(90,"USD")])
    }

    void testRemoveUnused() {
        def result=Money.fromMajor(10,"EUR") + Money.fromMajor(10,"USD")
        result += Money.fromMajor(10,"EUR")
        result += Money.fromMajor(-20,"EUR")
        assert result == Money.fromMajor(10,"USD")

        result += Money.fromMajor(-10,"USD")
        assert result == Money.fromMajor(0,"USD")
    }

    void testAddTwoBags() {
        def bag1=Money.fromMajor(10,"EUR") + Money.fromMajor(20,"USD")
        def bag2=Money.fromMajor(10,"FRF") + Money.fromMajor(20,"USD")

        MoneyBag result =  bag1 + bag2
        assert result.entries().values().containsAll([Money.fromMajor(10,"FRF"),Money.fromMajor(40,"USD"),Money.fromMajor(10,"EUR")])
    }

    void testNegate() {
        IMoney bag = Money.fromMajor(10,"EUR") + Money.fromMajor(20,"USD")
        assert bag.negate().entries().values().containsAll([Money.fromMajor(-10,"EUR"),Money.fromMajor(-20,"USD")])
    }
    void testMinusBag() {
        def bag = Money.fromMajor(10,"EUR") + Money.fromMajor(20,"USD")
        MoneyBag result = Money.fromMajor(20,"EUR") - bag
        assert result.entries().values().containsAll([Money.fromMajor(10,"EUR"), Money.fromMajor(-20,"USD")])
    }
    void testEmptyBag() {
        def bag = Money.fromMajor(10,"EUR") + Money.fromMajor(20,"USD")
        def result = bag - bag
        assert result.entries().isEmpty()
    }

    void testEquals() {
        def bag = Money.fromMajor(10,"EUR") + Money.fromMajor(20,"USD")
        assert bag.negate().negate() == bag
    }
}
