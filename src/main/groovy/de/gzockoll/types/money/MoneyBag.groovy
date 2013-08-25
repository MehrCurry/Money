package de.gzockoll.types.money

import com.ibm.icu.util.Currency as Currency
import groovy.transform.ToString

/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 16.08.13
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
@ToString
class MoneyBag extends AbstractMoney {
    final Map<Currency, MoneyBag> monies = [:]

    private MoneyBag(Collection<Money> coll) {
        coll?.each { appendMoney(it)}
    }

    private static IMoney create(AbstractMoney m1, AbstractMoney m2) {
        MoneyBag result = new MoneyBag();
        m1.appendTo(result);
        m2.appendTo(result);
        result.simplify();
    }

    public IMoney plus(IMoney m) {
        return m.addMoneyBag(this);
    }

    public IMoney addMoney(Money m) {
        appendMoney(m + getByCurrency(m.currency))
        simplify()
    }

    public IMoney addMoneyBag(MoneyBag s) {
        s.monies.values().each { this.addMoney(it) }
        simplify()
    }

    private void appendBag(MoneyBag aBag) {
        aBag.monies.values().each {appendMoney(it)}
    }

    private void appendMoney(Money aMoney) {
        if (aMoney.isZero())
            monies.remove(aMoney.currency)
        else
            monies.put(aMoney.currency,aMoney)
    }

    @Override
    public boolean equals(Object anObject) {
        if (isZero())
            if (anObject instanceof IMoney)
                return ((IMoney) anObject).isZero();

        if (anObject instanceof MoneyBag) {
            MoneyBag aMoneyBag = (MoneyBag) anObject;
            if (aMoneyBag.monies.size() != monies.size())
                return false;

            monies.values().each { each ->
                if (!aMoneyBag.contains(each))
                    return false;
            }
            return true;
        }
        return false;
    }

    public boolean isZero() {
        return monies.size() == 0;
    }

    public IMoney multiply(BigDecimal factor) {
        new MoneyBag(monies.values().collect { [ it * factor] })
    }

    public IMoney negate() {
        new MoneyBag(monies.values().collect { it.negate() })
    }

    private IMoney simplify() {
        if (monies.size() == 1)
            return monies.values().first()
        return this;
    }

    public IMoney minus(IMoney m) {
        return plus(m.negate());
    }

    public void appendTo(MoneyBag m) {
        m.appendBag(this);
    }

    boolean contains(Money aMoney) {
        getByCurrency(aMoney.currency) == aMoney
    }

    boolean containsCurrency(Currency currency) {
        monies.keySet().contains(currency)
    }

    Money getByCurrency(Currency currency) {
        return monies.get(currency,Money.fromMajor(0,currency))
    }

    Map<Currency,Money> entries() {
        monies.asImmutable()
    }
}
