package de.gzockoll.types.money
/**
 * Created with IntelliJ IDEA.
 * User: Guido Zockoll
 * Date: 16.08.13
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
public interface IMoney {
    /**
     * Adds a money to this money.
     */
    public abstract IMoney plus(IMoney m);
    /**
     * Adds a simple Money to this money. This is a helper method for
     * implementing double dispatch
     */
    public abstract IMoney addMoney(Money m);
    /**
     * Adds a MoneyBag to this money. This is a helper method for
     * implementing double dispatch
     */
    public abstract IMoney addMoneyBag(MoneyBag s);
    /**
     * Tests whether this money is zero
     */
    public abstract boolean isZero();
    /**
     * Multiplies a money by the given factor.
     */
    public abstract IMoney multiply(BigDecimal factor);
    /**
     * Negates this money.
     */
    public abstract IMoney negate();
    /**
     * Subtracts a money from this money.
     */
    public abstract IMoney minus(IMoney m);
}