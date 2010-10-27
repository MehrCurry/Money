package de.gzockoll.types.money;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.gzockoll.quantity.Quantity;

public class MoneyTest {

	@Before
	public void setUp() throws Exception {
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
		Money m = Money.cents(1);
		assertThat(m.getCurrency(), is(Currency.getInstance("EUR")));
		assertThat(m.getAmount().longValue(), is(1l));
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

//	@Test
//	public void testSubtract() {
//		Money m1 = Money.euros(10);
//		Money m2 = Money.euros(11);
//		assertThat(m2.sub(m1), is(Money.euros(1)));
//	}

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
		assertThat(Money.euros(10).toString(), is("10,00 Û (Scale: 0)"));
		assertThat(Money.dollars(10).toString(), is("10,00 USD (Scale: 0)"));
	}

	@Test
	public void testSimpleAllocation() {
		Money m = Money.euros(60);

		Money[] alloc = m.allocate(6);
		for (Money x : alloc) {
			assertThat(x, is(Money.euros(10)));
		}
	}
	
	@Test
	public void testRemainderAllocation() {
		Money m = Money.euros(59);

		Money[] alloc = m.allocate(3);
		assertThat(alloc[0], is(Money.cents(1967)));
		assertThat(alloc[1], is(Money.cents(1967)));
		assertThat(alloc[2], is(Money.cents(1966)));
		
	}

	@Test
	public void testAllocationWithRemainder() {
		Money m = Money.euros(59);

		Money[] alloc = m.allocate(3);
		assertThat(alloc[0], is(Money.cents(1967)));
		assertThat(alloc[1], is(Money.cents(1967)));
		assertThat(alloc[2], is(Money.cents(1966)));
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
		Money result = m.multiply(new BigDecimal("0.19"));
		System.out.println(result.getAmount().scale());
		assertThat(result, is(Money.cents(1900)));

	}
	
	@Test
	public void round()  {
		Money m=new Money(new BigDecimal("1234.567890"),CurrencyUnit.EURO);
		assertThat(m.round(),is(new Money(new BigDecimal("1235"),CurrencyUnit.EURO)));
		assertThat(m.round(2),is(new Money(new BigDecimal("1234.57"),CurrencyUnit.EURO)));
	}
	
	@Test
	public void zinsesZinz()  {
		BigDecimal zinzSatz = new BigDecimal("1.03");
		Money kapital=Money.euros(1000);
		for (int i=0;i<500;i++) {
			kapital=kapital.multiply(zinzSatz);
		}
		Money expected = new Money(new BigDecimal("262187723419"),CurrencyUnit.EURO);
		assertThat(kapital.round(),is(expected));
		
	}	
}
