package com.github.kamekoopa.j8utils.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class OptionTest {


	@RunWith(JUnit4.class)
	public static class 値がある時 {

		Optional<String> underlying;
		Option<String> option;

		@Before
		public void setUp() throws Exception {

			underlying = Optional.of("optional");
			option = Option.from(underlying);
		}

		@Test
		public void someを表す() throws Exception {
			assertTrue(option.isSome());
		}

		@Test
		public void noneを表さない() throws Exception {
			assertFalse(option.isNone());
		}

		@Test
		public void mapで値を写せる() throws Exception {

			Integer i = option.map(String::length).getOrElse(() -> -1);
			assertThat(i, is(8));
		}

		@Test
		public void mapeで値を写せる() throws Exception {

			Integer i = option.mape(String::length).getOrElse(() -> -1);
			assertThat(i, is(8));
		}

		@Test(expected = Exception.class)
		public void mapeでは例外を透過する() throws Exception {
			option.<Integer>mape(s -> {throw new Exception();}).getOrElse(() -> -1);
		}

		@Test
		public void flatMapで値を写せる() throws Exception {

			Integer i = option.flatMap(s -> Option.of(s.length())).getOrElse(() -> -1);
			assertThat(i, is(8));
		}

		@Test
		public void flatMapでnoneにできる() throws Exception {

			Option<Integer> none = option.flatMap(s -> Option.<Integer>none());
			assertTrue(none.isNone());
		}

		@Test
		public void getOrElseでくるまれている値を取得できる() throws Exception {

			String value = option.getOrElse(() -> "error");
			assertThat(value, is("optional"));
		}

		@Test
		public void 中身の値が等しければ等価になる() throws Exception {

			Option<String> test = Option.of("optional");
			assertThat(option, is(test));
		}

		@Test
		public void noneとは等しくない() throws Exception {

			assertThat(option, is(not(Option.none())));
		}
	}


	@RunWith(JUnit4.class)
	public static class 値がない時 {

		Optional<String> underlying;
		Option<String> option;

		@Before
		public void setUp() throws Exception {

			underlying = Optional.empty();
			option = Option.from(underlying);
		}

		@Test
		public void nullはNoneになる() throws Exception {
			assertTrue(Option.of(null).isNone());
		}

		@Test
		public void Optional_emptyはNoneになる() throws Exception {
			assertTrue(Option.from(Optional.empty()).isNone());
		}

		@Test
		public void noneを表す() throws Exception {
			assertTrue(option.isNone());
		}

		@Test
		public void someを表さない() throws Exception {
			assertFalse(option.isSome());
		}

		@Test
		public void mapしてもnoneのままになる() throws Exception {

			assertTrue(option.map(String::length).isNone());
		}

		@Test
		public void mapeしてもnoneのまま() throws Exception {

			assertTrue(option.mape(String::length).isNone());
		}

		@Test
		public void mapeで例外を投げても実行されないのでスローされない() throws Exception {
			assertTrue(option.<Integer>mape(s -> {
				throw new Exception();
			}).isNone());
		}

		@Test
		public void flatMapでも関数は実行されないのでsomeには出来ない() throws Exception {

			assertTrue(option.flatMap(s -> Option.of(s.length())).isNone());
		}

		@Test
		public void getOrElseでデフォルト値に指定した方の値が取れる() throws Exception {

			String value = option.getOrElse(() -> "error");
			assertThat(value, is("error"));
		}

		@Test
		public void noneと等しい() throws Exception {

			Option<String> test = Option.none();
			assertThat(option, is(test));
		}

		@Test
		public void someとは等しくない() throws Exception {

			Option<String> test = Option.of("test");
			assertThat(option, is(not(test)));
		}
	}
}