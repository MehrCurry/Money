package de.gzockoll.types.money;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.hamcrest.Matcher;

public class Money {
	private Currency currency;
	private long amount;

	@Override
	public String toString() {
		NumberFormat nf=NumberFormat.getCurrencyInstance();
		return nf.format(1.0 * (amount / centFactor()));
	}

	public Currency getCurrency() {
		return currency;
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
	
	private Money(double amount, Currency currency) {
		this.currency = currency;
		this.amount = Math.round(amount * centFactor());
	}

	private Money(long amount, Currency currency) {
		this.currency = currency;
		this.amount = amount * centFactor();
	}

	public Money(BigDecimal amount, Currency currency, int roundingMode) {
		this.currency=currency;
		amount.setScale(currency.getDefaultFractionDigits(),roundingMode);
		this.amount=(long) (amount.doubleValue() * centFactor());
	}

	private static final int[] cents = new int[] { 1, 10, 100, 1000 };

	private int centFactor() {
		return cents[currency.getDefaultFractionDigits()];
	}

	public int hashCode() {
		return (int) (amount ^ (amount >>> 32));
	}

	public boolean equals(Object other) {
		return (other instanceof Money) && equals((Money) other);
	}

	public boolean equals(Money other) {
		return currency.equals(other.currency) && (amount == other.amount);
	}

	public Money add(Money other) {
		assertSameCurrencyAs(other);
		return newMoney((amount + other.amount) / centFactor());
	}

	private void assertSameCurrencyAs(Money arg) {
		if (!currency.equals(arg.currency))
			throw new IllegalArgumentException("money math mismatch");
	}

	private Money newMoney(long amount) {
		Money money = new Money(amount, this.currency);
		return money;
	}

	public Money substract(Money other) {
		assertSameCurrencyAs(other);
		return newMoney((amount - other.amount) / centFactor());
	}

	public int compareTo(Object other) {
		return compareTo((Money) other);
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
		BigDecimal result = new BigDecimal(amount/centFactor()).multiply(factor);
		result.setScale(2,roundingMode);
		return new Money(result, currency,
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
		return new Money(d,currency);
	}

	public static Money euros(double d) {
		return new Money(d, Currency.getInstance("EUR"));
	}

	public static Money zero() {
		return new Money(0, getDefaultCurrency());
	}

	public boolean isZero() {
		return equals(Money.zero());
	}

	public Money negate() {
		return newMoney(-amount/centFactor(),currency);
	}

	public static Money dollars(int amount) {
		return newMoney(amount,Currency.getInstance("USD"));
	}
}
