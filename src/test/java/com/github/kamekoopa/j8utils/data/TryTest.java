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

import com.github.kamekoopa.j8utils.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.kamekoopa.j8utils.test.Tools.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(Enclosed.class)
public class TryTest {

	@RunWith(JUnit4.class)
	public static class 共通 {

		@Test
		public void ofの共変反変() throws Exception {

			SE<B> sup = () -> new B(1, 2);

			Try<A> _try = Try.<A>of(sup);

			assertTrue(_try.isSuccess());
			assertThat(_try.recover(e -> new B(0, 0)).a, is(1));
		}

		@Test
		public void mapの共変反変() throws Exception {

			Try<B> _try = Try.of(() -> new B(1, 2));

			Function<A, C> mapper = a -> new C(a.a, 10, 100);

			Try<B> mapped = _try.map(mapper);

			assertTrue(mapped.isSuccess());
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).a, is(1));
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).b, is(10));
		}

		@Test
		public void mapeの共変反変() throws Exception {

			Try<B> _try = Try.of(() -> new B(1, 2));

			FE1<A, C> mapper = a -> new C(a.a, 10, 100);

			Try<B> mapped = _try.mape(mapper);

			assertTrue(mapped.isSuccess());
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).a, is(1));
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).b, is(10));
		}

		@Test
		public void flatMapの共変反変() throws Exception {

			Try<B> _try = Try.of(() -> new B(1, 2));

			Function<A, Try<C>> mapper = a -> Try.of(() -> new C(a.a, 10, 100));

			Try<B> mapped = _try.flatMap(mapper);

			assertTrue(mapped.isSuccess());
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).a, is(1));
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).b, is(10));
		}

		@Test
		public void failableMapの共変反変() throws Exception {

			Try<B> _try = Try.of(() -> new B(1, 2));

			FE1<A, C> mapper = a -> new C(a.a, 10, 100);

			Try<B> mapped = _try.failableMap(mapper);

			assertTrue(mapped.isSuccess());
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).a, is(1));
			assertThat(mapped.fold(Function.identity(), e -> new B(0, 0)).b, is(10));
		}

		@Test
		public void foldの共変反変() throws Exception {

			Try<B> _try = Try.of(() -> new B(1, 2));

			Function<Exception, C> zero = e -> new C(0, 0, 0);
			Function<A, C> f = a -> new C(a.a, a.a, 100);

			B folded = _try.<B>fold(f, zero);

			assertThat(folded.a, is(1));
			assertThat(folded.b, is(1));
		}

		@Test
		public void recoverの共変反変() throws Exception {

			Try<B> _try = Try.of(() -> new B(1, 2));

			Function<Exception, C> recover = e -> new C(0, 0, 0);

			B recovered = _try.<B>recover(recover);

			assertThat(recovered.a, is(1));
			assertThat(recovered.b, is(2));
		}

		@Test
		public void ap1の共変反変() throws Exception {

			Try<B> t1 = Try.of(() -> new B(1, 2));
			Try<C> t2 = Try.of(() -> new C(10, 20, 30));

			BiFunction<A, A, C> f = (a1, a2) -> new C(a1.a, a1.a, a2.a);

			Try<B> ap = t1.<A, B>ap(t2, f);

			assertTrue(ap.isSuccess());
			assertThat(ap.recover(e -> new B(0, 0)).a, is(1));
			assertThat(ap.recover(e -> new B(0, 0)).b, is(1));
		}

		@Test
		public void ap2の共変反変() throws Exception {

			Try<B> t1 = Try.of(() -> new B(1, 2));
			Try<C> t2 = Try.of(() -> new C(10, 20, 30));
			Try<D> t3 = Try.of(() -> new D(100, 200, 300, 400));

			F3<A, A, A, D> f = (a, b, c) -> new D(a.a, a.a, b.a, c.a);

			Try<B> ap = t1.<A, A, B>ap(t2, t3, f);

			assertTrue(ap.isSuccess());
			assertThat(ap.recover(e -> new B(0, 0)).a, is(1));
			assertThat(ap.recover(e -> new B(0, 0)).b, is(1));
		}

		@Test
		public void ap3の共変反変() throws Exception {

			Try<B> t1 = Try.of(() -> new B(1, 2));
			Try<C> t2 = Try.of(() -> new C(10, 20, 30));
			Try<D> t3 = Try.of(() -> new D(100,200, 300, 400));
			Try<E> t4 = Try.of(() -> new E(1000,2000, 3000, 4000, 5000));

			F4<A, A, A, A, E> f = (a, b, c, d) -> new E(a.a, a.a, b.a, c.a, d.a);

			Try<B> ap = t1.<A, A, A, B>ap(t2, t3, t4, f);

			assertTrue(ap.isSuccess());
			assertThat(ap.recover(e -> new B(0, 0)).a, is(1));
			assertThat(ap.recover(e -> new B(0, 0)).b, is(1));
		}

		@Test
		public void ap4の共変反変() throws Exception {

			Try<B> t1 = Try.of(() -> new B(1, 2));
			Try<C> t2 = Try.of(() -> new C(10, 20, 30));
			Try<D> t3 = Try.of(() -> new D(100,200, 300, 400));
			Try<E> t4 = Try.of(() -> new E(1000,2000, 3000, 4000, 5000));
			Try<F> t5 = Try.of(() -> new F(10000,20000, 30000, 40000, 50000, 6000));

			F5<A, A, A, A, A, F> f = (a, b, c, d, e) -> new F(a.a, a.a, b.a, c.a, d.a, e.a);

			Try<B> ap = t1.<A, A, A, A, B>ap(t2, t3, t4, t5, f);

			assertTrue(ap.isSuccess());
			assertThat(ap.recover(e -> new B(0, 0)).a, is(1));
			assertThat(ap.recover(e -> new B(0, 0)).b, is(1));
		}
	}


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

		@Test
		public void unsafeGetで値が取れる() throws Exception {
			assertThat(this.test.unsafeGet(), is("success"));
		}

		@Test
		public void filterのpredicateがtrueならSuccess() throws Exception {

			assertTrue(
				this.test.filter((String str) -> !str.isEmpty(), Exception::new).isSuccess()
			);
		}

		@Test(expected = Exception.class)
		public void filterのpredicateがfalseなら指定した例外をくるんだFailure() throws Exception {
			this.test.filter(String::isEmpty, Exception::new).unsafeGet();
		}

		@Test
		public void peekで中身を覗ける() throws Exception {

			String[] mut = new String[]{""};

			this.test.peek(str -> mut[0] = str, e -> {throw new RuntimeException(e);});

			assertThat(mut[0], is("success"));
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
			}).fold(Function.identity(), Exception::getMessage);

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

			String result = test.recover(Exception::getMessage);

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

		@Test(expected = Exception.class)
		public void unsafeGetでくるまれていた例外がスローされる() throws Exception {
			this.test.unsafeGet();
		}

		@Test(expected = Exception.class)
		public void filterのpredicateがtrueでもFailure() throws Exception {

			Predicate<String> p = str -> !str.isEmpty();

			this.test.filter(p, Exception::new).unsafeGet();
		}

		@Test(expected = Exception.class)
		public void filterのpredicateがfalseでも先にくるまれている例外をスローする() throws Exception {

			this.test.filter(
				String::isEmpty,
				() -> new RuntimeException("error", null)
			).unsafeGet();
		}

		@Test
		public void peekで中身を覗ける() throws Exception {

			String[] mut = new String[]{""};
			this.test.peek(str -> mut[0] = str, e -> mut[0] = e.getMessage());

			assertThat(mut[0], is("failure"));
		}
	}
}