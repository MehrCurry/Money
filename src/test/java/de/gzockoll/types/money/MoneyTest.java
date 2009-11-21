package de.gzockoll.types.money;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

public class MoneyTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testContruction() {
		Money m1 = Money.euros(10);
		assertThat(m1.getAmount(), is(1000l));
		m1 = Money.euros(10);
		assertThat(m1.getAmount(), is(1000l));
		m1 = Money.newMoney(10.00, Currency.getInstance("EUR"));
		assertThat(m1.getAmount(), is(1000l));

		m1 = Money.newMoney(12.34, Currency.getInstance("EUR"));
		assertThat(m1.getAmount(), is(1234l));
		assertThat(m1, is(Money.euros(12.34)));

	}

	@Test
	public void testHashCode() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		assertThat(m1.hashCode() == m2.hashCode(), is(false));
		m1 = m1.add(Money.euros(1));
		assertThat(m1, is(m2));
		assertThat(m1.hashCode() == m2.hashCode(), is(true));
	}

	@Test
	public void testMoneyIntCurrency() {
		Money m = Money.euros(1);
		assertThat(m.getCurrency(), is(Currency.getInstance("EUR")));
		assertThat(m.getAmount(), is(100l));
	}

	@Test
	public void testAdd() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		assertThat(m1.add(m2), is(Money.euros(21)));
	}

	@Test
	public void testAddIsImmutable() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		Money m3 = m1.add(m2);
		assertThat(m3, is(Money.euros(21)));
		assertThat(m1, is(Money.euros(10)));
	}

	@Test
	public void testSubtract() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		assertThat(m2.substract(m1), is(Money.euros(1)));
	}

	@Test
	public void testEquals() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		Money m3 = Money.euros(11);
		assertThat(m1.equals(m2), is(false));
		assertThat(m2.equals(m1), is(false));
		assertThat(m1.equals(m3), is(false));
		assertThat(m3.equals(m1), is(false));
		assertThat(m3.equals(m2), is(true));
		assertThat(m2.equals(m3), is(true));

	}

	@Test
	public void testToString() {
		assertThat(Money.euros(10).toString(), is("10,00 €"));
	}

	@Test
	public void testSimpleAllication() {
		Money m = Money.euros(60);

		Money[] alloc = m.allocate(6);
		for (Money x : alloc) {
			assertThat(x, is(Money.euros(10)));
			System.out.println(x);
		}
	}

	@Test
	public void testMultiAllication() {
		Money m = Money.euros(60);

		Money[] alloc = m.allocate(new long[] { 3, 2, 1 });
		assertThat(alloc.length, is(3));
		assertThat(alloc[0], is(Money.euros(30)));
		assertThat(alloc[1], is(Money.euros(20)));
		assertThat(alloc[2], is(Money.euros(10)));
	}

	@Test
	public void testNegate() {
		Money m = Money.euros(10);
		assertThat(m.negate(), is(Money.euros(-10)));
		assertThat(m.negate().negate(), is(m));
	}

	@Test
	public void testMultiply() {
		Money m = Money.euros(100);
		assertThat(m.multiply(0.19), is(Money.euros(19)));
		m = Money.euros(95);
		assertThat(m.multiply(0.19), is(Money.euros(18.05)));

	}
}
