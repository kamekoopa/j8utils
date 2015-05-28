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
import com.github.kamekoopa.j8utils.test.Tools.*;
import com.github.kamekoopa.j8utils.utils.F3;
import com.github.kamekoopa.j8utils.utils.F4;
import com.github.kamekoopa.j8utils.utils.F5;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class FutureBuilderTest {

	public static void sleep(long milli){
		try {Thread.sleep(milli);} catch (InterruptedException e) {throw new RuntimeException(e);}
	}


	@RunWith(JUnit4.class)
	public static class 共通 {

		FutureBuilder builder;

		@Before
		public void setup() throws Exception {
			this.builder = FutureBuilder.build();
		}

		@Test
		public void run共変反変() throws Exception {

			Supplier<B> sup = () -> new B(1, 1);

			Try<A> t = this.builder.<A>run(sup).tryGet();

			assertTrue(t.isSuccess());
		}

		@Test
		public void map共変反変() throws Exception {

			Supplier<C> sup = () -> new C(1, 1, 1);
			Future<B> future = this.builder.<B>run(sup);

			Function<A, C> mapper = a -> new C(a.a, 10, 20);
			Try<B> t = future.<B>map(mapper).tryGet();

			assertTrue(t.isSuccess());
		}

		@Test
		public void flatMap共変反変() throws Exception {

			Supplier<C> sup = () -> new C(1, 1, 1);
			Future<B> future = this.builder.<B>run(sup);

			Function<A, Future<C>> mapper = a -> this.builder.run(() -> new C(a.a, 10, 20));
			Try<B> t = future.<B>flatMap(mapper).tryGet();

			assertTrue(t.isSuccess());
		}

		@Test
		public void ap1共変反変() throws Exception {

			Future<B> f1 = this.builder.run(() -> new B(1, 2));
			Future<C> f2 = this.builder.run(() -> new C(10, 20, 30));

			BiFunction<A, A, C> f = (a1, a2) -> new C(a1.a, a2.a, 0);

			Try<B> t = f1.<A, B>ap(f2, f).tryGet();

			assertTrue(t.isSuccess());
		}

		@Test
		public void ap2共変反変() throws Exception {

			Future<B> f1 = this.builder.run(() -> new B(1, 2));
			Future<C> f2 = this.builder.run(() -> new C(10, 20, 30));
			Future<D> f3 = this.builder.run(() -> new D(100, 200, 300, 400));

			F3<A, A, A, D> f = (a1, a2, a3) -> new D(a1.a, a2.a, a3.a, 0);

			Try<B> t = f1.<A, A, B>ap(f2, f3, f).tryGet();

			assertTrue(t.isSuccess());
		}

		@Test
		public void ap3共変反変() throws Exception {

			Future<B> f1 = this.builder.run(() -> new B(1, 2));
			Future<C> f2 = this.builder.run(() -> new C(10, 20, 30));
			Future<D> f3 = this.builder.run(() -> new D(100, 200, 300, 400));
			Future<E> f4 = this.builder.run(() -> new E(1000, 2000, 3000, 4000, 5000));

			F4<A, A, A, A, D> f = (a1, a2, a3, a4) -> new E(a1.a, a2.a, a3.a, a4.a, 0);

			Try<B> t = f1.<A, A, A, B>ap(f2, f3, f4, f).tryGet();

			assertTrue(t.isSuccess());
		}

		@Test
		public void ap4共変反変() throws Exception {

			Future<B> f1 = this.builder.run(() -> new B(1, 2));
			Future<C> f2 = this.builder.run(() -> new C(10, 20, 30));
			Future<D> f3 = this.builder.run(() -> new D(100, 200, 300, 400));
			Future<E> f4 = this.builder.run(() -> new E(1000, 2000, 3000, 4000, 5000));
			Future<F> f5 = this.builder.run(() -> new F(10000, 20000, 30000, 40000, 50000, 60000));

			F5<A, A, A, A, A, D> f = (a1, a2, a3, a4, a5) -> new F(a1.a, a2.a, a3.a, a4.a, a5.a, 0);

			Try<B> t = f1.<A, A, A, A, B>ap(f2, f3, f4, f5, f).tryGet();

			assertTrue(t.isSuccess());
		}
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