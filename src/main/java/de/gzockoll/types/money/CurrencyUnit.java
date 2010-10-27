package de.gzockoll.types.money;

import java.util.Currency;

import org.apache.commons.lang.builder.EqualsBuilder;

import de.gzockoll.quantity.Quantity;
import de.gzockoll.quantity.Unit;

public class CurrencyUnit implements Unit {
	public static CurrencyUnit EURO=new CurrencyUnit(Currency.getInstance("EUR"));

	private Currency currency;

	public CurrencyUnit(Currency currency) {
		this.currency=currency;
	}

	public Currency getCurrency() {
		return currency;
	}
	
	@Override
	public String toString() {
		return currency.toString();
	}
	
	@Override
	public int hashCode() {
		return 2*currency.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

//	public Quantity amount(long l) {
//		return new Money(l,this);
//	}
//
	public Money getZeroQuantity() {
		return Money.euros(0);
	}

	public static CurrencyUnit getInstance(String currencyCode) {
		return new CurrencyUnit(Currency.getInstance(currencyCode));
	}
}	
