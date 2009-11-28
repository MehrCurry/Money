package de.gzockoll.types.money;


import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.*;
import static org.junit.Assert.*;

import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

public class CurrencyUnitTest {
	private static final String _ID = "$Id$";

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testHashCode() {
		CurrencyUnit c1=new CurrencyUnit(Currency.getInstance("EUR"));
		CurrencyUnit c2=new CurrencyUnit(Currency.getInstance("USD"));
		assertThat(c1.hashCode(),not(equalTo(c2.hashCode())));
		assertThat(c1.hashCode(),equalTo(CurrencyUnit.EURO.hashCode()));
	}
	
	@Test
	public void testToString() {
		assertThat(CurrencyUnit.EURO.toString(),is("EUR"));
	}
}
