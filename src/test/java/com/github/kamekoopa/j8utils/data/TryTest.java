/*
 * Copyright 2015 kamekoopa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
		public void mapeは例外を透過する() throws Throwable {
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

			String result = test.recover(e -> "failure");
			assertThat(result, is("success"));
		}

		@Test
		public void apで複数のsuccessに関数を適用できる() throws Exception {

			Try<String> tb = Try.of(() -> "1");
			Try<String> tc = Try.of(() -> "2");
			Try<String> td = Try.of(() -> "3");

			Try<String> actual = test.ap(tb, tc, td, (a, b, c, d) -> a + b + c + d);

			assertThat(actual.recover(e -> "error"), is("success123"));
		}

		@Test
		public void apで渡すtryの中にfailureがあると全体としてfailureになる() throws Exception {

			Try<String> tb = Try.of(() -> "1");
			Try<String> tc = Try.failure(new Exception("error"));
			Try<String> td = Try.of(() -> "3");
			Try<String> te = Try.of(() -> "4");

			Try<String> actual = test.ap(tb, tc, td, te, (a, b, c, d, e) -> a + b + c + d + e);

			assertTrue(actual.isFailure());
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
		public void mapeは実行されないので関数内で例外を投げても何も起きない() throws Throwable {

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

			String result = test.recover(Throwable::getMessage);

			assertThat(result, is("failure"));
		}

		@Test
		public void apで複数のsomeに関数適用をしても全体としてnone() throws Exception {

			Try<String> tb = Try.of(() -> "1");
			Try<String> tc = Try.of(() -> "2");

			Try<String> actual = test.ap(tb, tc, (a, b, c) -> a + b + c);
			assertTrue(actual.isFailure());
		}

		@Test
		public void apで渡すoptionがnoneならnone() throws Exception {

			Try<String> tb = Try.failure(new Exception("error"));

			Try<String> actual = test.ap(tb, (a, b) -> a + b);

			assertTrue(actual.isFailure());
		}
	}
}