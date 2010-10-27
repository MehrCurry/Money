package de.gzockoll.types.money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.gzockoll.quantity.Quantity;
import de.gzockoll.quantity.Unit;

public class Money implements Quantity<BigDecimal> {
	private BigDecimal amount;
	private CurrencyUnit unit;
	
	
	public Money(BigDecimal amount, CurrencyUnit unit) {
		super();
		this.amount = amount;
		this.unit = unit;
	}

	
	public Currency getCurrency() {
		return ((CurrencyUnit) unit).getCurrency();
	}

	private static CurrencyUnit getDefaultCurrencyUnit() {
		return new CurrencyUnit(getCurrencyByLocale(Locale.getDefault()));
	}

	private static Currency getCurrencyByLocale(Locale l) {
		return Currency.getInstance(l);
	}
	
	private static final int[] cents = new int[] { 1, 10, 100, 1000 };
	private static final MathContext ROUNDING_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

	private static int centFactor(Currency currency) {
		return cents[currency.getDefaultFractionDigits()];
	}

	public Money multiply(double factor) {
		return multiply(new BigDecimal(factor));
	}

	public Money multiply(BigDecimal factor) {
		return new Money(amount.multiply(factor), unit);
	}

	public Money[] allocate(int n) {
		long lowResult = amount.longValue()/n;
		long highResult = lowResult+1;
		Money[] results = new Money[n];
		int remainder = (int) (amount.longValue() % n);
		for (int i = 0; i < remainder; i++)
			results[i] = new Money(new BigDecimal(highResult),unit);
		for (int i = remainder; i < n; i++)
			results[i] = new Money(new BigDecimal(lowResult),unit);
		return results;
	}

	private Money newMoney(BigDecimal amount) {
		return new Money(amount,getDefaultCurrencyUnit());
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
			results[i] = new Money(amount.multiply(new BigDecimal(ratios[i])).divide(new BigDecimal(total)),unit);
			// results[i] = new Money(amount.multiply(new BigDecimal(ratios[i]).divide(new BigDecimal(total))),unit);
			remainder -= results[i].amount.longValue();
		}
		for (int i = 0; i < remainder; i++) {
			results[i].amount.add(BigDecimal.ONE);
		}
		return results;
	}

	public static Money euros(int i) {
		CurrencyUnit u = CurrencyUnit.getInstance("EUR");
		return new Money(new BigDecimal(i*centFactor(u.getCurrency())), u);
	}


	public static Money euros(double d) {
		CurrencyUnit u = CurrencyUnit.getInstance("EUR");
		return new Money(new BigDecimal(d*centFactor(u.getCurrency())), u);
	}

	public static Money zero() {
		return new Money(BigDecimal.ZERO, getDefaultCurrencyUnit());
	}

	public boolean isZero() {
		return equals(Money.zero());
	}

	public static Money dollars(int amount) {
		CurrencyUnit u = new CurrencyUnit(Currency.getInstance("USD"));
		return new Money(new BigDecimal(amount * centFactor(u.getCurrency())),u);
	}

	public static Money cents(int i) {
		CurrencyUnit u = getDefaultCurrencyUnit();
		return new Money(new BigDecimal(i),u);
	}

	public Unit getUnit() {
		return unit;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Money negate() {
		return new Money(amount.negate(),unit);
	}

	public Money add(Quantity<BigDecimal> other) {
		assertSameCurrency(other);
		return new Money(amount.add(other.getAmount()),unit);
	}

	private void assertSameCurrency(Quantity<BigDecimal> other) {
		if (!unit.equals(other.getUnit()))
			throw new IllegalArgumentException("Incompatible Currencies: " + unit + " and " + other.getUnit());
	}


	public Quantity<BigDecimal> sub(Quantity<BigDecimal> other) {
		assertSameCurrency(other);
		return new Money(amount.subtract(other.getAmount()),unit);
	}

	public int compareTo(Quantity<BigDecimal> other) {
		assertSameCurrency(other);
		return amount.compareTo(other.getAmount());
	}
	
	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setCurrency(unit.getCurrency());

		return nf.format(amount.divide(new BigDecimal(100)).doubleValue()) + " (Scale: " + amount.scale() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Money))
			return false;
		if (this==obj)
			return true;
		Money other=(Money) obj;
		return new EqualsBuilder().append(amount.compareTo(other.getAmount())==0,true).append(unit.getCurrency(), other.getCurrency()).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(amount).append(unit.getCurrency()).hashCode();
	}
	
	public Money round() {
		return round(0);
	}

	public Money round(int scale) {
		BigDecimal a=getAmount();
		a=a.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return new Money(a,unit);
	}
}
