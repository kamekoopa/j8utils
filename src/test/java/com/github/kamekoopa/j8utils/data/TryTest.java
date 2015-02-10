package com.github.kamekoopa.j8utils.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(Enclosed.class)
public class TryTest {


	@RunWith(JUnit4.class)
	public static class 成功 {

		Try<String> test;

		@Before
		public void setup() throws Exception {
			this.test = Try.of(() -> "success");
		}

		@Test
		public void 成功を表す() throws Exception {
			assertTrue(test.isSuccess());
		}

		@Test
		public void 失敗を表さない() throws Exception {
			assertFalse(test.isFailure());
		}

		@Test
		public void mapで値を変更できる() throws Exception {

			Integer result = test.map(String::length)
				.fold(Function.identity(), e -> -1);

			assertThat(result, is(7));
		}

		@Test(expected = Exception.class)
		public void mapeは例外を透過する() throws Exception {
			test.mape(s -> {
				throw new Exception();
			});
		}

		@Test
		public void failableMapでFailureにできる() throws Exception {

			Try<Object> result = test.failableMap(s -> {
				throw new Exception();
			});

			assertTrue(result.isFailure());
		}

		@Test
		public void flatMapで値を変更できる() throws Exception {

			Integer result = test.flatMap(s -> Try.success(s.length()))
				.fold(Function.identity(), e -> -1);

			assertThat(result, is(7));
		}

		@Test
		public void flatMapでFailureにできる() throws Exception {

			Try<Integer> result = test.<Integer>flatMap(s -> Try.failure(new Exception()));

			assertTrue(result.isFailure());
		}

		@Test
		public void optionにするとsomeになる() throws Exception {

			assertTrue(test.toOption().isSome());
		}

		@Test
		public void eitherにするとrightになる() throws Exception {

			assertTrue(test.toEither().isRight());
		}

		@Test
		public void fallbackに指定した値は無視されて中身が取得できる() throws Exception {

			String result = test.fallback(e -> "failure");
			assertThat(result, is("success"));
		}
	}

	@RunWith(JUnit4.class)
	public static class 失敗 {

		Try<String> test;

		@Before
		public void setup() throws Exception {
			this.test = Try.of(() -> { throw new Exception("failure"); });
		}

		@Test
		public void 成功を表さない() throws Exception {
			assertFalse(test.isSuccess());
		}

		@Test
		public void 失敗を表す() throws Exception {
			assertTrue(test.isFailure());
		}

		@Test
		public void mapは実行されない() throws Exception {

			Integer result = test.map(String::length)
				.fold(Function.identity(), e -> -1);

			assertThat(result, is(-1));
		}

		@Test
		public void mapeは実行されないので関数内で例外を投げても何も起きない() throws Exception {

			test.mape(s -> {
				throw new Exception();
			});
		}

		@Test
		public void failableMapの関数は実行されないので何も変わらない() throws Exception {

			String result = test.<String>failableMap(s -> {
				throw new Exception();
			}).fold(Function.identity(), Throwable::getMessage);

			assertThat(result, is("failure"));
		}

		@Test
		public void flatMapは実行されないので値を変更できない() throws Exception {

			Try<Integer> result = test.flatMap(s -> Try.success(s.length()));

			assertTrue(result.isFailure());
		}

		@Test
		public void optionにするとnoneになる() throws Exception {

			assertTrue(test.toOption().isNone());
		}

		@Test
		public void eitherにするとleftになる() throws Exception {

			assertTrue(test.toEither().isLeft());
		}

		@Test
		public void fallbackで失敗状態から値を取得できる() throws Exception {

			String result = test.fallback(Throwable::getMessage);

			assertThat(result, is("failure"));
		}
	}
}