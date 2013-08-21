package de.gzockoll.types.money

/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 21.08.13
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractMoney implements IMoney {
    /**
     * Append this to a MoneyBag m.
     * appendTo() needs to be public because it is used
     * polymorphically, but it should not be used by clients
     * because it modifies the argument m.
     */
    protected abstract void appendTo(MoneyBag m);
}
