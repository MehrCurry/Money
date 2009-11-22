package de.gzockoll.types.money;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import de.gzockoll.quantity.Quantity;
import de.gzockoll.quantity.Unit;

public class Money extends Quantity {

	public CurrencyUnit getUnit() {
		return (CurrencyUnit) unit;
	}
	
	@Override
	public String toString() {
		NumberFormat nf=NumberFormat.getCurrencyInstance();
		return nf.format(1.0 * (amount / centFactor(((CurrencyUnit) unit).getCurrency())));
	}

	public Currency getCurrency() {
		return ((CurrencyUnit) unit).getCurrency();
	}

	public long getAmount() {
		return amount;
	}

	private Money(long amount) {
		this(amount,getDefaultCurrency());
	}

	private static Currency getDefaultCurrency() {
		return getCurrencyByLocale(Locale.getDefault());
	}

	private static Currency getCurrencyByLocale(Locale l) {
		return Currency.getInstance(l);
	}
	
	private Money(double amount, CurrencyUnit unit) {
		super(Math.round(amount * centFactor(unit.getCurrency())),unit);
	}

	private Money(long amount, Currency currency) {
		super(amount * centFactor(currency), new CurrencyUnit(currency));
	}

	public Money(BigDecimal amount, CurrencyUnit unit, int roundingMode) {
		super((long) (amount.doubleValue() * centFactor(unit.getCurrency())), unit);;
		// amount=amount.setScale(unit.getCurrency().getDefaultFractionDigits(),roundingMode);
	}

	private static final int[] cents = new int[] { 1, 10, 100, 1000 };

	private static int centFactor(Currency currency) {
		return cents[currency.getDefaultFractionDigits()];
	}

	public int hashCode() {
		return (int) (amount ^ (amount >>> 32));
	}

	public boolean equals(Object other) {
		return (other instanceof Money) && equals((Money) other);
	}

	public boolean equals(Money other) {
		return super.equals(other);
	}

	public Money add(Money other) {
		assertSameCurrencyAs(other);
		return newMoney((amount + other.amount) / centFactor(getCurrency()));
	}

	private void assertSameCurrencyAs(Money arg) {
		if (!unit.equals(arg.unit))
			throw new IllegalArgumentException("money math mismatch");
	}

	private Money newMoney(long amount) {
		Money money = new Money(amount, getUnit());
		return money;
	}

	public Money substract(Money other) {
		assertSameCurrencyAs(other);
		return newMoney((amount - other.amount) / centFactor(getCurrency()));
	}

	public int compareTo(Money other) {
		assertSameCurrencyAs(other);
		if (amount < other.amount)
			return -1;
		else if (amount == other.amount)
			return 0;
		else
			return 1;
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
		BigDecimal result = new BigDecimal(amount/centFactor(getCurrency())).multiply(factor);
		result.setScale(2,roundingMode);
		return new Money(result, getUnit(),
						roundingMode);
	}

	public Money[] allocate(int n) {
		Money lowResult = newMoney(amount / n / centFactor());
		Money highResult = newMoney(lowResult.amount + 1);
		Money[] results = new Money[n];
		int remainder = (int) amount % n;
		for (int i = 0; i < remainder; i++)
			results[i] = highResult;
		for (int i = remainder; i < n; i++)
			results[i] = lowResult;
		return results;
	}

	private long centFactor() {
		return centFactor(getCurrency());
	}

	public Money[] allocate(long[] ratios) {
		long total = 0;
		for (int i = 0; i < ratios.length; i++)
			total += ratios[i];
		long remainder = amount;
		Money[] results = new Money[ratios.length];
		for (int i = 0; i < results.length; i++) {
			results[i] = newMoney(amount * ratios[i] / total / centFactor());
			remainder -= results[i].amount;
		}
		for (int i = 0; i < remainder; i++) {
			results[i].amount++;
		}
		return results;
	}

	public static Money euros(int i) {
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

	public Money negate() {
		return newMoney(-amount/centFactor(),getUnit());
	}

	private Money newMoney(long l, CurrencyUnit unit) {
		return new Money(l,unit);
	}

	public static Money dollars(int amount) {
		return newMoney(amount,Currency.getInstance("USD"));
	}
}
