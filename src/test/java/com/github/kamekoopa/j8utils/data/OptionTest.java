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

import com.github.kamekoopa.j8utils.test.Tools.*;
import com.github.kamekoopa.j8utils.utils.F3;
import com.github.kamekoopa.j8utils.utils.F4;
import com.github.kamekoopa.j8utils.utils.F5;
import com.github.kamekoopa.j8utils.utils.FE1;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class OptionTest {

	@RunWith(JUnit4.class)
	public static class 共通 {

		@Test
		public void mapの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			Function<A, C> mapper = a -> new C(a.a, 10, 100);

			Option<B> mapped = option.map(mapper);

			assertTrue(mapped.isSome());
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).a, is(1));
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).b, is(10));
		}

		@Test
		public void mapeの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			FE1<A, C> mapper = a -> new C(a.a, 10, 100);

			Option<B> mapped = option.mape(mapper);

			assertTrue(mapped.isSome());
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).a, is(1));
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).b, is(10));
		}

		@Test
		public void flatMapの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			Function<A, Option<C>> mapper = a -> Option.of(new C(a.a, 10, 100));

			Option<B> mapped = option.<B>flatMap(mapper);

			assertTrue(mapped.isSome());
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).a, is(1));
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).b, is(10));
		}

		@Test
		public void flatMapeの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			FE1<A, Option<C>> mapper = a -> Option.of(new C(a.a, 10, 100));

			Option<B> mapped = option.<B>flatMape(mapper);

			assertTrue(mapped.isSome());
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).a, is(1));
			assertThat(mapped.fold(() -> new B(0, 0), Function.identity()).b, is(10));
		}

		@Test
		public void foldの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			Supplier<C> zero = () -> new C(0, 0, 0);
			Function<A, C> f = a -> new C(a.a, a.a, 100);

			B folded = option.<B>fold(zero, f);

			assertThat(folded.a, is(1));
			assertThat(folded.b, is(1));
		}

		@Test
		public void getOrElseの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			Supplier<C> zero = () -> new C(0, 0, 0);

			B get = option.<B>getOrElse(zero);

			assertThat(get.a, is(1));
			assertThat(get.b, is(2));
		}

		@Test
		public void orの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			Supplier<Option<C>> zero = () -> Option.of(new C(10, 20, 30));

			Option<B> get = option.or(zero);

			assertTrue(get.isSome());
			assertThat(get.fold(() -> new B(0, 0), Function.identity()).a, is(1));
			assertThat(get.fold(() -> new B(0, 0), Function.identity()).b, is(2));
		}

		@Test
		public void ifEmptyの共変反変() throws Exception {

			Option<B> option = Option.of(new B(1, 2));

			Supplier<C> zero = () -> new C(0, 0, 0);

			Option<B> get = option.<B>ifEmpty(zero);

			assertTrue(get.isSome());
			assertThat(get.fold(() -> new B(0, 0), Function.identity()).a, is(1));
			assertThat(get.fold(() -> new B(0, 0), Function.identity()).b, is(2));
		}

		@Test
		public void ap1の共変反変() throws Exception {

			Option<B> option1 = Option.of(new B(1, 2));
			Option<C> option2 = Option.of(new C(10, 20, 30));

			BiFunction<A, A, C> f = (a1, a2) -> new C(a1.a, a1.a, a2.a);

			Option<B> ap = option1.<A, B>ap(option2, f);

			assertTrue(ap.isSome());
			assertThat(ap.getOrElse(() -> new B(0, 0)).a, is(1));
			assertThat(ap.getOrElse(() -> new B(0, 0)).b, is(1));
		}

		@Test
		public void ap2の共変反変() throws Exception {

			Option<B> option1 = Option.of(new B(1, 2));
			Option<C> option2 = Option.of(new C(10, 20, 30));
			Option<D> option3 = Option.of(new D(100,200, 300, 400));

			F3<A, A, A, D> f = (a, b, c) -> new D(a.a, a.a, b.a, c.a);

			Option<B> ap = option1.<A, A, B>ap(option2, option3, f);

			assertTrue(ap.isSome());
			assertThat(ap.getOrElse(() -> new B(0, 0)).a, is(1));
			assertThat(ap.getOrElse(() -> new B(0, 0)).b, is(1));
		}

		@Test
		public void ap3の共変反変() throws Exception {

			Option<B> option1 = Option.of(new B(1, 2));
			Option<C> option2 = Option.of(new C(10, 20, 30));
			Option<D> option3 = Option.of(new D(100,200, 300, 400));
			Option<E> option4 = Option.of(new E(1000,2000, 3000, 4000, 5000));

			F4<A, A, A, A, E> f = (a, b, c, d) -> new E(a.a, a.a, b.a, c.a, d.a);

			Option<B> ap = option1.<A, A, A, B>ap(option2, option3, option4, f);

			assertTrue(ap.isSome());
			assertThat(ap.getOrElse(() -> new B(0, 0)).a, is(1));
			assertThat(ap.getOrElse(() -> new B(0, 0)).b, is(1));
		}

		@Test
		public void ap4の共変反変() throws Exception {

			Option<B> option1 = Option.of(new B(1, 2));
			Option<C> option2 = Option.of(new C(10, 20, 30));
			Option<D> option3 = Option.of(new D(100,200, 300, 400));
			Option<E> option4 = Option.of(new E(1000,2000, 3000, 4000, 5000));
			Option<F> option5 = Option.of(new F(10000,20000, 30000, 40000, 50000, 6000));

			F5<A, A, A, A, A, F> f = (a, b, c, d, e) -> new F(a.a, a.a, b.a, c.a, d.a, e.a);

			Option<B> ap = option1.<A, A, A, A, B>ap(option2, option3, option4, option5, f);

			assertTrue(ap.isSome());
			assertThat(ap.getOrElse(() -> new B(0, 0)).a, is(1));
			assertThat(ap.getOrElse(() -> new B(0, 0)).b, is(1));
		}
	}


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
		public void filterでpredicateがtrueならそのままSome() throws Exception {

			Option<String> actual = option.filter(s -> s.equals("optional"));

			assertTrue(actual.isSome());
			assertThat(actual.unsafeGet(), is("optional"));
		}

		@Test
		public void filterでpredicateがfalseならNone() throws Exception {

			Option<String> actual = option.filter(s -> !s.equals("optional"));

			assertTrue(actual.isNone());
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

		@Test(expected = Exception.class)
		public void flatMap例外透過版は例外を透過する() throws Exception {
			option.flatMape(s -> {
				throw new Exception("");
			});
		}

		@Test
		public void getOrElseでくるまれている値を取得できる() throws Exception {

			String value = option.getOrElse(() -> "error");
			assertThat(value, is("optional"));
		}

		@Test
		public void unsafeGetで値が取れる() throws Exception {

			assertThat(option.unsafeGet(), is("optional"));
		}

		@Test
		public void peekで中身を覗ける() throws Exception {

			String[] mut = {""};
			option.peek(() -> mut[0] = "none", s -> mut[0] = s);

			assertThat(mut[0], is("optional"));
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

		@Test
		public void 要素1個のstreamにできる() throws Exception {

			String[] str = {""};

			option.stream().forEach(s -> str[0] = s);

			assertThat(str[0], is("optional"));
		}

		@Test
		public void apで複数のsomeに関数を適用できる() throws Exception {

			Option<String> ob = Option.of("1");
			Option<String> oc = Option.of("2");
			Option<String> od = Option.of("3");

			Option<String> actual = option.ap(ob, oc, od, (a, b, c, d) -> a + b + c + d);

			assertThat(actual.getOrElse(() -> "error"), is("optional123"));
		}

		@Test
		public void apで渡すoptionの中にnoneがあると全体としてnoneになる() throws Exception {

			Option<String> ob = Option.of("1");
			Option<String> oc = Option.none();
			Option<String> od = Option.of("3");
			Option<String> oe = Option.of("4");

			Option<String> actual = option.ap(ob, oc, od, oe, (a, b, c, d, e) -> a + b + c + d + e);

			assertTrue(actual.isNone());
		}

		@Test
		public void ifEmptyで自分自身が取得できる() throws Exception {

			Option<String> actual = option.ifEmpty(() -> "ifEmpty");

			assertThat(actual, is(option));
		}

		@Test
		public void orで自分自身が取得できる() throws Exception {

			Option<String> actual = option.or(() -> Option.of("ifEmpty"));
			assertThat(actual, is(option));
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
		public void filterでpredicateがtrueでもNone() throws Exception {

			Option<String> actual = option.filter(s -> true);

			assertTrue(actual.isNone());
		}

		@Test
		public void filterでpredicateがfalseでもNone() throws Exception {

			Option<String> actual = option.filter(s -> false);

			assertTrue(actual.isNone());
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
		public void flatMap例外透過版で例外を投げても実行されないのでスローされない() throws Exception {
			assertTrue(
				option.flatMape(s -> {
					throw new Exception("");
				}).isNone()
			);
		}

		@Test
		public void getOrElseでデフォルト値に指定した方の値が取れる() throws Exception {

			String value = option.getOrElse(() -> "error");
			assertThat(value, is("error"));
		}

		@Test(expected = NoSuchElementException.class)
		public void unsafeGetで例外がスローされる() throws Exception {
			option.unsafeGet();
		}

		@Test
		public void peekで中身を覗ける() throws Exception {

			String[] mut = {""};
			option.peek(() -> mut[0] = "none", s -> mut[0] = s);

			assertThat(mut[0], is("none"));
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

		@Test
		public void 要素0個のstreamにできる() throws Exception {

			String[] str = {"none"};

			option.stream().forEach(s -> str[0] = s);

			assertThat(str[0], is("none"));
		}

		@Test
		public void apで複数のsomeに関数適用をしても全体としてnone() throws Exception {

			Option<String> ob = Option.of("1");
			Option<String> oc = Option.of("2");

			Option<String> actual = option.ap(ob, oc, (a, b, c) -> a + b + c);
			assertTrue(actual.isNone());
		}

		@Test
		public void apで渡すoptionがnoneならnone() throws Exception {

			Option<String> ob = Option.none();

			Option<String> actual = option.ap(ob, (a, b) -> a + b);

			assertTrue(actual.isNone());
		}

		@Test
		public void ifEmptyにSomeを指定すると引数の方の値が取得できる() throws Exception {

			Option<String> actual = option.ifEmpty(() -> "ifEmpty");

			assertThat(actual, is(Option.of("ifEmpty")));
		}

		@Test
		public void ifEmptyにnullを指定するとNoneが取得できる() throws Exception {

			Option<String> actual = option.ifEmpty(() -> null);

			assertThat(actual, is(Option.none()));
		}

		@Test
		public void orにSomeを指定すると指定したoptionが取得できる() throws Exception {

			Option<String> actual = option.or(() -> Option.of("ifEmpty"));
			assertThat(actual, is(Option.of("ifEmpty")));
		}

		@Test
		public void orにnoneを指定するとnoneが取得できる() throws Exception {

			Option<String> actual = option.or(Option::none);
			assertThat(actual, is(Option.none()));
		}
	}
}