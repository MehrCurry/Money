package de.gzockoll.types.money;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MoneyTest {
    public static Currency EUR = Currency.getInstance("EUR");

    @Before
    public void setUp() throws Exception {
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
    public void testAdd() {
        Money m1 = Money.euros(10);
        Money m2 = Money.euros(11);
        Money m3 = (Money) m1.add(m2);
        assertThat(m3, is(Money.euros(21)));
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
    public void testSimpleAllocation() {
        Money m = Money.euros(60);

        Money[] alloc = m.allocate(6);
        for (Money x : alloc) {
            assertThat(x, is(Money.euros(10)));
            System.out.println(x);
        }
    }

    @Test
    public void testMinor() {
        Money m1 = Money.fromMinor(1234, EUR);
        assertThat(m1.asMinor(), is(1234l));

        Money m2 = Money.fromMajor(12, EUR);
        assertThat(m2.asMinor(), is(1200l));
    }

    @Test
    public void testRemainderAllocation() {
        Money m = Money.euros(59);

        Money[] alloc = m.allocate(3);
        assertThat(alloc[0], is(Money.fromMinor(1967, EUR)));
        assertThat(alloc[1], is(Money.fromMinor(1967, EUR)));
        assertThat(alloc[2], is(Money.fromMinor(1966, EUR)));
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
    public void testMultiAllocationWithRemainder() {
        Money m = Money.fromMinor(6002, EUR);

        Money[] alloc = m.allocate(new long[] { 3, 2, 1 });
        assertThat(alloc.length, is(3));
        assertThat(alloc[0], is(Money.fromMinor(3001, EUR)));
        assertThat(alloc[1], is(Money.fromMinor(2001, EUR)));
        assertThat(alloc[2], is(Money.euros(10)));
    }

    @Test
    public void testNegate() {
        Money m = Money.euros(10);
        assertThat((Money) m.negate(), is(Money.euros(-10)));
        assertThat((Money) m.negate().negate(), is(m));
    }

    @Test
    public void testMultiply() {
        Money m = Money.euros(100);
        Money expected = Money.euros(19);
        Money result = m.multiply(0.19);
        assertThat(result, is(expected));
        m = Money.euros(95);
        result = m.multiply(0.19);
        assertThat(result, is(Money.fromMinor(1805, EUR)));
    }

    @Test
    public void testConstuctors() {
        Money m1 = Money.fromMajor(19, EUR);
        Money m2 = Money.fromMinor(1900, EUR);
        assertThat(m1, is(m2));
    }

    @Test
    public void testZinseszins() {
        Money m = Money.euros(100);
        for (int i = 0; i < 10; i++) {
            System.out.println(m);
            m = m.multiply(1.03);
        }
    }
}
