package de.gzockoll.types.money;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.junit.Assert.*;

import java.util.Currency;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.gzockoll.quantity.Quantity;
import de.gzockoll.quantity.SimpleQuantity;

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
		m1 = (Money) m1.add(Money.euros(1));
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
		Money m3 = (Money) m1.add(m2);
		assertThat(m3, is(Money.euros(21)));
	}
	
	public void testAddReturnType() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		Quantity result=m1.add(m2);
		assertThat(result,instanceOf(Money.class));
	}

	public void testSubtractReturnType() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		Quantity result=m2.sub(m1);
		assertThat(result,instanceOf(Money.class));
	}

	@Test
	public void testAddIsImmutable() {
		Money m1 = Money.euros(10);
		Money m2 = Money.euros(11);
		Money m3 = (Money) m1.add(m2);
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
	@Ignore
	public void testToString() {
		assertThat(Money.euros(10).toString(), is("10,00 €"));
	}

	@Test
	public void testSimpleAllocation() {
		Money m = Money.euros(60);

		Money[] alloc = m.allocate(6);
		for (Money x : alloc) {
			assertThat(x, is(Money.euros(10)));
			System.out.println(x);
		}
	}
	
	@Test
	public void testRemainderAllocation() {
		Money m = Money.euros(59);

		Money[] alloc = m.allocate(3);
		assertThat(alloc[0], is(Money.euros(19.00)));
		assertThat(alloc[1], is(Money.euros(20.00)));
		assertThat(alloc[2], is(Money.euros(20.00)));
		
	}

	@Test
	public void testAllocationWithRemainder() {
		Money m = Money.euros(59);

		Money[] alloc = m.allocate(3);
		assertThat(alloc[0], is(Money.euros(19.67)));
		assertThat(alloc[1], is(Money.euros(19.67)));
		assertThat(alloc[2], is(Money.euros(19.66)));
	}
	@Test
	public void testMultiAllocation() {
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
		assertThat((Money)m.negate(), is(Money.euros(-10)));
		assertThat((Money)m.negate().negate(), is(m));
	}

	@Test
	public void testMultiply() {
		Money m = Money.euros(100);
		assertThat(m.multiply(0.19), is(Money.euros(19)));
		m = Money.euros(95);
		assertThat(m.multiply(0.19), is(Money.euros(18.05)));
	}
	
	@Test
	public void testConstuctors() {
		Money m1=new Money(1900 ,CurrencyUnit.EURO);
		Money m2=new Money(19.00,CurrencyUnit.EURO);
		assertThat(m1,is(m2));
	}
	
	@Test
	public void testMoneyFromQuantity() {
		Quantity q=new SimpleQuantity(1900, CurrencyUnit.EURO);
		Money m=new Money(q);
		assertThat(m,is(new Money(1900,CurrencyUnit.EURO)));
	}
}
