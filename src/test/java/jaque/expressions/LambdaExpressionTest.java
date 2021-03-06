/*
 * Copyright Konstantin Triger <kostat@gmail.com> 
 * 
 * This file is part of Jaque - JAva QUEry library <http://code.google.com/p/jaque/>.
 * 
 * Jaque is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaque is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jaque.expressions;

import static org.junit.Assert.*;
import jaque.expression.LambdaExpression;

import java.util.Date;
import java.util.function.*;

import org.junit.Test;

public class LambdaExpressionTest {

	// @Test
	// public void testGetBody() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetParameters() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testParseNew() throws Throwable {
		Predicate<java.util.Date> pp1 = new Predicate<Date>() {
			
			@Override
			public boolean test(Date t) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		Class<? extends Predicate> class1 = pp1.getClass();
		class1.getName();
		Predicate<java.util.Date> pp = d -> d.after(new java.sql.Time(System.currentTimeMillis()));
		LambdaExpression<Predicate<java.util.Date>> le = LambdaExpression.parse(pp);
		
		Date anotherDate = new Date();
		
		pp = d -> d.compareTo(anotherDate) < 10;
		le = LambdaExpression.parse(pp);
		
		Function<Object[], ?> fr = le.compile();
		
		Date date = new Date();
		assertEquals(pp.test(date), fr.apply(new Object[]{date}));
		//Predicate<java.util.Date> le = LambdaExpression.parse(pp);
//		le = LambdaExpression.parse(pp).compile();
//
//		assertTrue(le.invoke(new java.sql.Date(System.currentTimeMillis()
//				+ (5 * 1000))));
//		assertFalse(le.invoke(new java.sql.Date(System.currentTimeMillis()
//				- (5 * 1000))));
	}

	@Test
	public void testParseP() throws Throwable {
		Predicate<Float> pp = t -> t > 6 ? t < 12 : t > 2;
		LambdaExpression<Predicate<Float>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(4f), le.apply(new Object[]{4f}));
		assertEquals(pp.test(7f), le.apply(new Object[]{7f}));
		assertEquals(pp.test(14f), le.apply(new Object[]{14f}));
		assertEquals(pp.test(12f), le.apply(new Object[]{12f}));
		assertEquals(pp.test(6f), le.apply(new Object[]{6f}));
		assertEquals(pp.test(Float.NaN), le.apply(new Object[]{Float.NaN}));
	}

	@Test
	public void testParseP1() throws Throwable {
		Predicate<String> pp = t -> t.equals("abc");
		LambdaExpression<Predicate<String>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test("abc"), le.apply(new Object[]{"abc"}));
		assertEquals(pp.test("abC"), le.apply(new Object[]{"abC"}));
	}

	@Test
	public void testParseP2() throws Throwable {
		final Object[] ar = new Object[] { 5 };

		Predicate<Integer> pp = t -> (ar.length << t) == (1 << 5) && ar[0] instanceof Number;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(4), le.apply(new Object[]{4}));
	}
	
	@Test
	public void testParseThis() throws Throwable {

		Predicate<Integer> pp = t -> this != null;

		LambdaExpression<Predicate<Integer>> lambda = LambdaExpression
				.parse(pp);

		Function<Object[], ?> le = lambda.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
	}
	
	@Test
	public void testParseP3() throws Throwable {
		final Object[] ar = new Object[] { 5f };

		Predicate<Integer> pp = t -> ar[0] instanceof Float || (ar.length << t) == (1 << 5);

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(4), le.apply(new Object[]{4}));
	}
	
	@Test
	public void testParseField() throws Throwable {
		Predicate<Object[]> pp = t -> t.length == 3;

		LambdaExpression<Predicate<Object[]>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		Integer[] ar1 = { 2, 3, 4 };
		Integer[] ar2 = { 2, 4 };
		
		assertEquals(pp.test(ar1), le.apply(new Object[]{ar1}));
		assertEquals(pp.test(ar2), le.apply(new Object[]{ar2}));
	}
	
	@Test
	public void testParse0() throws Throwable {
		Supplier<Float> pp = () -> 23f;

		LambdaExpression<Supplier<Float>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertTrue(23f == (Float)le.apply(null));
		assertFalse(24f == (Float)le.apply(null));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testParseIllegal() throws Throwable {

		try {
			final Object[] x = new Object[1];
			Supplier<Float> pp = () -> {
					x[0] = null;
					return 23f;
			};
			LambdaExpression<Supplier<Float>> parsed = LambdaExpression.parse(pp);
			Function<Object[], ?> le = parsed.compile();

			le.apply(null);
		} catch (Throwable e) {
			assertTrue(e.getMessage().indexOf("AASTORE") >= 0);
			throw e;
		}
	}
	
	@Test
	public void testParse2() throws Throwable {
		BiFunction<Float, Float, Boolean> pp = (Float t, Float r) -> t > 6 ? r < 12 : t > 2;

		LambdaExpression<BiFunction<Float, Float, Boolean>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.apply(7f, 10f), le.apply(new Object[]{7f, 10f}));
		assertEquals(pp.apply(7f, 14f), le.apply(new Object[]{7f, 14f}));
	}
	
	@Test
	public void testParse4() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 ? r > 1 : r < 4) || (r instanceof Number);

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(11), le.apply(new Object[]{11}));
	}
	
	@Test
	public void testParse5() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 ? r > 1 : r < 4) || (r > 25 ? r > 28 : r < 32)
						|| (r < 23 ? r > 15 : r < 17);

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(11), le.apply(new Object[]{11}));
		assertEquals(pp.test(29), le.apply(new Object[]{29}));
		assertEquals(pp.test(26), le.apply(new Object[]{26}));
		assertEquals(pp.test(18), le.apply(new Object[]{18}));
		assertEquals(pp.test(14), le.apply(new Object[]{14}));
	}
	
	@Test
	public void testParse6() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 ? r > 1 : r < 4) && (r > 25 ? r > 28 : r < 32)
						|| (r < 23 ? r > 15 : r < 17);

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(11), le.apply(new Object[]{11}));
		assertEquals(pp.test(29), le.apply(new Object[]{29}));
		assertEquals(pp.test(26), le.apply(new Object[]{26}));
		assertEquals(pp.test(18), le.apply(new Object[]{18}));
		assertEquals(pp.test(14), le.apply(new Object[]{14}));
	}
	
	@Test
	public void testParse7() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 && r > 25) || r < 23;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(11), le.apply(new Object[]{11}));
		assertEquals(pp.test(29), le.apply(new Object[]{29}));
		assertEquals(pp.test(26), le.apply(new Object[]{26}));
		assertEquals(pp.test(18), le.apply(new Object[]{18}));
		assertEquals(pp.test(14), le.apply(new Object[]{14}));
	}
	
	@Test
	public void testParse8() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 || r > 25) && r < 23;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(11), le.apply(new Object[]{11}));
		assertEquals(pp.test(29), le.apply(new Object[]{29}));
		assertEquals(pp.test(26), le.apply(new Object[]{26}));
		assertEquals(pp.test(18), le.apply(new Object[]{18}));
		assertEquals(pp.test(14), le.apply(new Object[]{14}));
	}
	
	@Test
	public void testParse9() throws Throwable {
		Predicate<Integer> pp = r -> (r < 6 || r > 25) && r < 23 || r > 25;

		LambdaExpression<Predicate<Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.test(5), le.apply(new Object[]{5}));
		assertEquals(pp.test(11), le.apply(new Object[]{11}));
		assertEquals(pp.test(29), le.apply(new Object[]{29}));
		assertEquals(pp.test(26), le.apply(new Object[]{26}));
		assertEquals(pp.test(18), le.apply(new Object[]{18}));
		assertEquals(pp.test(14), le.apply(new Object[]{14}));
	}
	

	@Test
	public void testParse10() throws Throwable {
		Function<Integer, Integer> pp = r -> ~r;

		LambdaExpression<Function<Integer, Integer>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.apply(5), le.apply(new Object[]{5}));
		assertEquals(pp.apply(-10), le.apply(new Object[]{-10}));
		assertEquals(pp.apply(29), le.apply(new Object[]{29}));
		assertEquals(pp.apply(26), le.apply(new Object[]{26}));
		assertEquals(pp.apply(-18), le.apply(new Object[]{-18}));
		assertEquals(pp.apply(14), le.apply(new Object[]{14}));
	}
	
	@Test
	public void testParse11() throws Throwable {
		Function<Integer, Byte> pp = r -> (byte) (int) r;

		LambdaExpression<Function<Integer, Byte>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		assertEquals(pp.apply(5), le.apply(new Object[]{5}));
		assertEquals(pp.apply(-10), le.apply(new Object[]{-10}));
		assertEquals(pp.apply(29), le.apply(new Object[]{29}));
		assertEquals(pp.apply(26), le.apply(new Object[]{26}));
		assertEquals(pp.apply(-18), le.apply(new Object[]{-18}));
		assertEquals(pp.apply(144567), le.apply(new Object[]{144567}));
		assertEquals(pp.apply(-144567), le.apply(new Object[]{-144567}));
	}
	
	@Test(expected = NullPointerException.class)
	public void testParse12() throws Throwable {
		Function<Integer, Byte> pp = r -> (byte) (int) r;

		LambdaExpression<Function<Integer, Byte>> parsed = LambdaExpression.parse(pp);
		Function<Object[], ?> le = parsed.compile();

		le.apply(null);
	}

	// @Test
	// public void testGetExpressionType() {
	// fail("Not yet implemented");
	// }

	// @Test
	// public void testGetResultType() {
	// fail("Not yet implemented");
	// }

}
