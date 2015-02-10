package com.github.kamekoopa.j8utils.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class LazyValTest {


	@Test
	public void getするまで評価されない() throws Exception {

		boolean[] value = { false };
		LazyVal.of(() -> value[0] = true);

		assertFalse(value[0]);
	}

	@Test
	public void getすると評価される() throws Exception {

		boolean[] value = { false };
		LazyVal.of(() -> value[0] = true).get();

		assertTrue(value[0]);
	}

	@Test
	public void 一度評価すると何度取得しても同じ値が取得できる() throws Exception {

		LazyVal<Date> lazyVal = LazyVal.of(Date::new);

		Date date1 = lazyVal.get();
		Date date2 = lazyVal.get();

		assertThat(date1, is(sameInstance(date2)));
	}

	@Test(expected = RuntimeException.class)
	public void 値を遅延提供するsupplierが例外を投げる時get時に実行時例外にラップされてスローされる() throws Exception {
		LazyVal.of(() -> { throw new Exception(); }).get();
	}

	@Test(expected = Exception.class)
	public void 値を遅延提供するsupplierが例外を投げる時gete時に例外をスローする() throws Throwable {
		LazyVal.of(() -> { throw new Exception(); }).gete();
	}
}