package de.gzockoll.types.money;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import de.gzockoll.quantity.BigDecimalQuantity;
import de.gzockoll.quantity.Quantity;
import de.gzockoll.quantity.Unit;

public class Money extends  BigDecimalQuantity {
	
	public CurrencyUnit getUnit() {
		return (CurrencyUnit) unit;
	}
	
//	@Override
//	public String toString() {
//		NumberFormat nf=NumberFormat.getCurrencyInstance();
//		return nf.format(divide(centFactor(((CurrencyUnit) unit).getCurrency())));
//	}

	public Currency getCurrency() {
		return ((CurrencyUnit) unit).getCurrency();
	}

	public Money(long amount) {
		this(amount,getDefaultCurrency());
	}

	private static Currency getDefaultCurrency() {
		return getCurrencyByLocale(Locale.getDefault());
	}

	private static Currency getCurrencyByLocale(Locale l) {
		return Currency.getInstance(l);
	}
	
	public Money(double amount, CurrencyUnit unit) {
		super(new BigDecimal(amount),unit);
	}

	public Money(long amount, Currency currency) {
		super(new BigDecimal(amount), new CurrencyUnit(currency));
	}

	public Money(long amount, CurrencyUnit unit) {
		super(new BigDecimal(amount), unit);
	}

//	public Money(BigDecimal amount, CurrencyUnit unit, int roundingMode) {
//		super(amount, unit);;
//	}


	public Money(Quantity q) {
		super((BigDecimal)q.getAmount(),(CurrencyUnit) q.getUnit());
	}

	public Money(BigDecimal val, Unit unit) {
		super(val,unit);
	}

	private static final int[] cents = new int[] { 1, 10, 100, 1000 };

	private static int centFactor(Currency currency) {
		return cents[currency.getDefaultFractionDigits()];
	}

	public boolean equals(Object other) {
		return (other instanceof Money) && equals((Money) other);
	}

	public boolean equals(Money other) {
		return super.equals(other);
	}

	private Quantity newMoneyInternal(long l, CurrencyUnit unit) {
		return new Money(l/centFactor(unit.getCurrency()),unit);
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
		return new Money(super.add(other));
	}

	public Money substract(Money other) {
		return new Money(super.sub(other));
	}

	public boolean greaterThan(Money other) {
		return (compareTo(other) > 0);
	}

	public Money multiply(double factor) {
		return multiply(new BigDecimal(factor));
	}

	public Money multiply(BigDecimal factor) {
		return multiply(factor, BigDecimal.ROUND_HALF_EVEN);
	}

	public Money multiply(BigDecimal factor, int roundingMode) {
		BigDecimal result = amount.divide(factor.multiply(new BigDecimal(centFactor(getCurrency()))));
		// result.setScale(2,roundingMode);
		return new Money(result, getUnit());
	}

	public Money[] allocate(int n) {
		Money lowResult = newMoney(amount.longValue()/n);
		Money highResult = newMoney(lowResult.amount.add(BigDecimal.ONE));
		Money[] results = new Money[n];
		int remainder = (int) amount.longValue() % n;
		for (int i = 0; i < remainder; i++)
			results[i] = highResult;
		for (int i = remainder; i < n; i++)
			results[i] = lowResult;
		return results;
	}

	private Money newMoney(BigDecimal val) {
		return new Money(val,unit);
	}

	private long centFactor() {
		return centFactor(getCurrency());
	}

	public Money[] allocate(long[] ratios) {
		long total = 0;
		for (int i = 0; i < ratios.length; i++)
			total += ratios[i];
		long remainder = amount.longValue();
		Money[] results = new Money[ratios.length];
		for (int i = 0; i < results.length; i++) {
			results[i] = newMoney(amount.longValue() * ratios[i] / total);
			remainder -= results[i].amount.longValue();
		}
		for (int i = 0; i < remainder; i++) {
			// results[i].amount++;
		}
		return results;
	}

	public static Money euros(int i) {
		Currency eur = Currency.getInstance("EUR");
		return newMoney(i*centFactor(eur), eur);
	}

	public static Money cents(int i) {
		return newMoney(i, Currency.getInstance("EUR"));
	}

	public static Money newMoney(double d, Currency currency) {
		return new Money(d,new CurrencyUnit(currency));
	}

	public static Money euros(double d) {
		return new Money(d, CurrencyUnit.EURO);
	}

	public static Money zero() {
		return new Money(0, getDefaultCurrency());
	}

	public boolean isZero() {
		return equals(Money.zero());
	}

	private Money newMoney(long l, CurrencyUnit unit) {
		return new Money(l,unit);
	}

	public static Money dollars(int amount) {
		return newMoney(amount,Currency.getInstance("USD"));
	}

	public Quantity newInstance(Number amount, Unit unit) {
		return new Money(new BigDecimal(amount.doubleValue()),(CurrencyUnit) unit);
	}

	public Quantity newInstanceFromQuantity(Quantity a) {
		return new Money(new BigDecimal(a.getAmount().doubleValue()),(CurrencyUnit) a.getUnit());
	}

}
