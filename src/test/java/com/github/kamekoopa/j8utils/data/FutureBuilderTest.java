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

import com.github.kamekoopa.j8utils.data.FutureBuilder.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Enclosed.class)
public class FutureBuilderTest {

	public static void sleep(long milli){
		try {Thread.sleep(milli);} catch (InterruptedException e) {throw new RuntimeException(e);}
	}

	@RunWith(JUnit4.class)
	public static class ExecutorServiceで実行する場合 {

		FutureBuilder builder;

		@Before
		public void setup() throws Exception {
			this.builder = FutureBuilder.buildWith(Executors.newFixedThreadPool(3));
		}

		@Test
		public void 非同期処理ができる() throws Exception {

			String result = builder.run(() -> "future")
				.tryGet().fold(Function.identity(), s -> "error");

			assertThat(result, is("future"));
		}

		@Test
		public void futureに次の処理を合成できる() throws Exception {

			Integer result = builder.run(() -> "future")
				.map(String::length)
				.tryGet().fold(Function.identity(), s -> -1);

			assertThat(result, is(6));
		}

		@Test
		public void futureでflatMapできる() throws Exception {

			Integer result = builder.run(() -> "future")
				.flatMap(s -> FutureBuilder.build().run(s::length))
				.tryGet().fold(Function.identity(), s -> -1);

			assertThat(result, is(6));
		}

		@Test
		public void 途中で例外が発生した場合getで第二引数の関数が利用される() throws Exception {

			String result = builder.run(() -> "future")
				.<String>map(s -> {
					throw new RuntimeException("error");
				})
				.tryGet().fold(s -> s, Exception::getMessage);

			assertThat(result, is("error"));
		}

		@Test
		public void タイムアウトまでに処理が終わらなければタイムアウト例外が取得される() throws Exception {

			String result = builder.run(() -> "future")
				.map(s -> {
					sleep(5 * 1000L);
					return s;
				})
				.tryGet(1L, TimeUnit.SECONDS).fold(s -> getClass().getName(), e -> e.getClass().getName());

			assertThat(result, is("java.util.concurrent.TimeoutException"));
		}

		@Test
		public void apで複数Futureの合成ができる() throws Exception {

			Future<String> fa = builder.run(() -> {
				sleep(1000L);
				return "a";
			});
			Future<String> fb = builder.run(() -> {
				return "b";
			});
			Future<String> fc = builder.run(() -> {
				sleep(500L);
				return "c";
			});

			Future<String> actual = fa.ap(fb, fc, (a, b, c) -> a + b + c);

			assertThat(actual.tryGet().recover(Exception::getMessage), is("abc"));
		}

		@Test
		public void apで一部のFutureがエラー終了した場合getでFailureになる() throws Exception {

			Future<String> fa = builder.run(() -> {
				sleep(1000L);
				return "a";
			});
			Future<String> fb = builder.run(() -> {
				throw new RuntimeException("error");
			});
			Future<String> fc = builder.run(() -> {
				sleep(500L);
				return "c";
			});

			Future<String> actual = fa.ap(fb, fc, (a, b, c) -> a + b + c);

			assertThat(actual.tryGet().recover(Exception::getMessage), is("error"));
		}
	}


	@RunWith(JUnit4.class)
	public static class ForkJoinPoolで実行する場合 {

		FutureBuilder builder;

		@Before
		public void setup() throws Exception {
			this.builder = FutureBuilder.build();
		}

		@Test
		public void 非同期処理ができる() throws Exception {

			String result = builder.run(() -> "future")
				.tryGet().fold(Function.identity(), s -> "error");

			assertThat(result, is("future"));
		}

		@Test
		public void futureに次の処理を合成できる() throws Exception {

			Integer result = builder.run(() -> "future")
				.map(String::length)
				.tryGet().fold(Function.identity(), s -> -1);

			assertThat(result, is(6));
		}

		@Test
		public void futureでflatMapできる() throws Exception {

			Integer result = builder.run(() -> "future")
				.flatMap(s -> FutureBuilder.build().run(s::length))
				.tryGet().fold(Function.identity(), s -> -1);

			assertThat(result, is(6));
		}

		@Test
		public void 途中で例外が発生した場合getで第二引数の関数が利用される() throws Exception {

			String result = builder.run(() -> "future")
				.<String>map(s -> {
					throw new RuntimeException("error");
				})
				.tryGet().fold(s -> s, Exception::getMessage);

			assertThat(result, is("error"));
		}

		@Test
		public void タイムアウトまでに処理が終わらなければタイムアウト例外が取得される() throws Exception {

			String result = builder.run(() -> "future")
				.map(s -> {
					sleep(5 * 1000L);
					return s;
				})
				.tryGet(1L, TimeUnit.SECONDS).fold(s -> getClass().getName(), e -> e.getClass().getName());

			assertThat(result, is("java.util.concurrent.TimeoutException"));
		}

		@Test
		public void apで複数Futureの合成ができる() throws Exception {

			Future<String> fa = builder.run(() -> {
				sleep(1000L);
				return "a";
			});
			Future<String> fb = builder.run(() -> {
				return "b";
			});
			Future<String> fc = builder.run(() -> {
				sleep(500L);
				return "c";
			});

			Future<String> actual = fa.ap(fb, fc, (a, b, c) -> a + b + c);

			assertThat(actual.tryGet().recover(Exception::getMessage), is("abc"));
		}

		@Test
		public void apで一部のFutureがエラー終了した場合getでFailureになる() throws Exception {

			Future<String> fa = builder.run(() -> {
				sleep(1000L);
				return "a";
			});
			Future<String> fb = builder.run(() -> {
				throw new RuntimeException("error");
			});
			Future<String> fc = builder.run(() -> {
				sleep(500L);
				return "c";
			});

			Future<String> actual = fa.ap(fb, fc, (a, b, c) -> a + b + c);

			assertThat(actual.tryGet().recover(Exception::getMessage), is("error"));
		}
	}
}