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
		public void mapeで値を写せる() throws Throwable {

			Integer i = option.mape(String::length).getOrElse(() -> -1);
			assertThat(i, is(8));
		}

		@Test(expected = Exception.class)
		public void mapeでは例外を透過する() throws Throwable {
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
		public void flatMap例外透過版は例外を透過する() throws Throwable {
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
		public void ifEmptyで自分自身が取得できる() throws Throwable {

			Option<String> actual = option.ifEmpty(() -> "ifEmpty");

			assertThat(actual, is(option));
		}

		@Test
		public void orで自分自身が取得できる() throws Throwable {

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
		public void mapしてもnoneのままになる() throws Exception {

			assertTrue(option.map(String::length).isNone());
		}

		@Test
		public void mapeしてもnoneのまま() throws Throwable {

			assertTrue(option.mape(String::length).isNone());
		}

		@Test
		public void mapeで例外を投げても実行されないのでスローされない() throws Throwable {
			assertTrue(option.<Integer>mape(s -> {
				throw new Exception();
			}).isNone());
		}

		@Test
		public void flatMapでも関数は実行されないのでsomeには出来ない() throws Throwable {

			assertTrue(option.flatMap(s -> Option.of(s.length())).isNone());
		}

		@Test
		public void flatMap例外透過版で例外を投げても実行されないのでスローされない() throws Throwable {
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
		public void ifEmptyにSomeを指定すると引数の方の値が取得できる() throws Throwable {

			Option<String> actual = option.ifEmpty(() -> "ifEmpty");

			assertThat(actual, is(Option.of("ifEmpty")));
		}

		@Test
		public void ifEmptyにnullを指定するとNoneが取得できる() throws Throwable {

			Option<String> actual = option.ifEmpty(() -> null);

			assertThat(actual, is(Option.none()));
		}

		@Test
		public void orにSomeを指定すると指定したoptionが取得できる() throws Throwable {

			Option<String> actual = option.or(() -> Option.of("ifEmpty"));
			assertThat(actual, is(Option.of("ifEmpty")));
		}

		@Test
		public void orにnoneを指定するとnoneが取得できる() throws Throwable {

			Option<String> actual = option.or(() -> Option.none());
			assertThat(actual, is(Option.none()));
		}
	}
}