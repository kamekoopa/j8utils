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

package com.github.kamekoopa.j8utils.utils;


import com.github.kamekoopa.j8utils.data.Option;
import com.github.kamekoopa.j8utils.data.Tuple2;
import com.github.kamekoopa.j8utils.test.Tools.A;
import com.github.kamekoopa.j8utils.test.Tools.B;
import com.github.kamekoopa.j8utils.test.Tools.C;
import com.github.kamekoopa.j8utils.test.Tools.D;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.kamekoopa.j8utils.utils.Utils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;


@RunWith(Enclosed.class)
public class UtilsTest {

	@RunWith(JUnit4.class)
	public static class リストが空の時 {

		List<String> list;
		Stream<String> stream;

		@Before
		public void setup() {
			this.list = new ArrayList<>();
			this.stream = list.stream();
		}

		@Test(expected = NoSuchElementException.class)
		public void headで最初の要素を取得しようとすると例外が発生する() throws Exception {
			head(list);
		}

		@Test
		public void headOptionでNoneが取得できる() throws Exception {

			Option<String> option = headOption(list);
			assertTrue(option.isNone());
		}

		@Test(expected = NoSuchElementException.class)
		public void tailを取得しようとすると例外が発生する() throws Exception {
			tail(list);
		}

		@Test
		public void getで要素を取得しようとするとNoneで取得できる() throws Exception {

			Option<String> option = get(list, 0);
			assertTrue(option.isNone());
		}

		@Test
		public void 空のリストとzipすると空のリストが返る() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(list, new ArrayList<>());
			assertThat(zip, is(empty()));
		}

		@Test
		public void 空のstreamとzipした結果のstreamをリストに集約すると空のリスト() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(stream, new ArrayList<Integer>().stream())
				.collect(Collectors.toList());

			assertThat(zip, is(empty()));
		}

		@Test
		public void 空じゃないリストとzipすると空のリストが返る() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(list, Arrays.asList(1, 2, 3));
			assertThat(zip, is(empty()));
		}

		@Test
		public void 空じゃないstreamとzipした結果のstreamをリストに集約すると空のリスト() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(stream, Arrays.asList(1, 2, 3).stream())
				.collect(Collectors.toList());

			assertThat(zip, is(empty()));
		}

		@Test
		public void zipWithIndexすると空のリストが返る() throws Exception {

			List<Tuple2<Integer, String>> zipWithIndex = zipWithIndex(list);
			assertThat(zipWithIndex, is(empty()));
		}

		@Test
		public void 空のstreamをzipWithIndexすると空のリストが返る() throws Exception {

			List<Tuple2<Integer, String>> zipWithIndex = zipWithIndex(stream).collect(Collectors.toList());
			assertThat(zipWithIndex, is(empty()));
		}
	}


	@RunWith(JUnit4.class)
	public static class リストに要素が存在する時 {

		List<String> list;
		Stream<String> stream;

		@Before
		public void setup() {
			this.list = Arrays.asList("first", "second", "third");
			this.stream = list.stream();
		}

		@Test
		public void headで最初の要素が取得できる() throws Exception {

			String e = head(list);
			assertThat(e, is("first"));
		}

		@Test
		public void headOptionで最初の要素をくるんだSomeが取得できる() throws Exception {

			Option<String> option = headOption(list);
			assertTrue(option.isSome());
			assertThat(option.getOrElse(() -> ""), is("first"));
		}

		@Test
		public void tailで最初以外の要素が取得できる() throws Exception {

			List<String> tail = tail(list);
			assertThat(tail, is(Arrays.asList("second", "third")));
		}

		@Test
		public void getでSomeにくるまれた要素が取得できる() throws Exception {

			Option<String> option = get(list, 2);
			assertTrue(option.isSome());
			assertThat(option.getOrElse(() -> ""), is("third"));
		}

		@Test
		public void 要素数が少ないリストとzipするとあふれた要素は除かれて結合される() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(list, Arrays.asList(1, 2));
			assertThat(zip.size(), is(2));
			assertThat(zip.get(0)._1, is("first"));
			assertThat(zip.get(0)._2, is(1));
			assertThat(zip.get(1)._1, is("second"));
			assertThat(zip.get(1)._2, is(2));
		}

		@Test
		public void 要素数が少ないstreamとzipするとあふれた要素は除かれて結合される() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(stream, Arrays.asList(1, 2).stream())
				.collect(Collectors.toList());

			assertThat(zip.size(), is(2));
			assertThat(zip.get(0)._1, is("first"));
			assertThat(zip.get(0)._2, is(1));
			assertThat(zip.get(1)._1, is("second"));
			assertThat(zip.get(1)._2, is(2));
		}

		@Test
		public void 要素数が大きいリストとzipすると自分の要素は全て結合される() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(list, Arrays.asList(1, 2, 3, 4));
			assertThat(zip.size(), is(3));
			assertThat(zip.get(0)._1, is("first"));
			assertThat(zip.get(0)._2, is(1));
			assertThat(zip.get(1)._1, is("second"));
			assertThat(zip.get(1)._2, is(2));
			assertThat(zip.get(2)._1, is("third"));
			assertThat(zip.get(2)._2, is(3));
		}

		@Test
		public void 要素数が大きいstreamとzipすると自分の要素は全て結合される() throws Exception {

			List<Tuple2<String, Integer>> zip = zip(stream, Arrays.asList(1, 2, 3, 4).stream())
				.collect(Collectors.toList());

			assertThat(zip.size(), is(3));
			assertThat(zip.get(0)._1, is("first"));
			assertThat(zip.get(0)._2, is(1));
			assertThat(zip.get(1)._1, is("second"));
			assertThat(zip.get(1)._2, is(2));
			assertThat(zip.get(2)._1, is("third"));
			assertThat(zip.get(2)._2, is(3));
		}

		@Test
		public void zipWithIndexすると0から始まるindexと結合されたリストが返る() throws Exception {

			List<Tuple2<Integer, String>> zipWithIndex = zipWithIndex(list);

			assertThat(zipWithIndex.size(), is(3));
			assertThat(zipWithIndex.get(0)._1, is(0));
			assertThat(zipWithIndex.get(0)._2, is("first"));
			assertThat(zipWithIndex.get(1)._1, is(1));
			assertThat(zipWithIndex.get(1)._2, is("second"));
			assertThat(zipWithIndex.get(2)._1, is(2));
			assertThat(zipWithIndex.get(2)._2, is("third"));
		}

		@Test
		public void streamをzipWithIndexすると0から始まるindexと結合されたリストが返る() throws Exception {

			List<Tuple2<Integer, String>> zipWithIndex = zipWithIndex(stream).collect(Collectors.toList());

			assertThat(zipWithIndex.size(), is(3));
			assertThat(zipWithIndex.get(0)._1, is(0));
			assertThat(zipWithIndex.get(0)._2, is("first"));
			assertThat(zipWithIndex.get(1)._1, is(1));
			assertThat(zipWithIndex.get(1)._2, is("second"));
			assertThat(zipWithIndex.get(2)._1, is(2));
			assertThat(zipWithIndex.get(2)._2, is("third"));
		}
	}


	@RunWith(JUnit4.class)
	public static class リストがNoneとSomeが混在するOptionの要素を持つ時 {

		List<Option<String>> list;

		@Before
		public void setUp() throws Exception {

			this.list = new ArrayList<Option<String>>(){{
				add(Option.of("a"));
				add(Option.none());
				add(Option.of("c"));
				add(Option.of("d"));
			}};
		}

		@Test
		public void filterSomeで要素がSomeのもののみOptionが剥がされたリストになる() throws Exception {

			List<String> actual = filterSome(this.list);

			assertThat(actual.get(0), is("a"));
			assertThat(actual.get(1), is("c"));
			assertThat(actual.get(2), is("d"));
		}

		@Test
		public void filterSomeで要素がSomeのもののみOptionが剥がされたリストになる_Stream版() throws Exception {

			List<String> actual = filterSome(this.list.stream());

			assertThat(actual.get(0), is("a"));
			assertThat(actual.get(1), is("c"));
			assertThat(actual.get(2), is("d"));
		}

		@Test
		public void filterSomeで要素がSomeのもののみOptionが剥がされたリストになる_戻り値もStream版() throws Exception {

			List<String> actual = filterSomeToStream(this.list.stream()).collect(Collectors.toList());

			assertThat(actual.get(0), is("a"));
			assertThat(actual.get(1), is("c"));
			assertThat(actual.get(2), is("d"));
		}
	}


	@RunWith(JUnit4.class)
	public static class リスト共通 {

		List<B> list;
		Stream<B> stream;

		@Before
		public void setup() {
			this.list = Arrays.asList(new B(0, 0), new B(1, 1), new B(2, 2));
			this.stream = list.stream();
		}

		@Test
		public void get共変反変() throws Exception {

			Option<A> optA = get(list, 0);

			assertTrue(optA.isSome());
		}

		@Test
		public void head共変反変() throws Exception {

			A a = Utils.<A>head(list);

			assertThat(a.a, is(0));
		}

		@Test
		public void headOption共変反変() throws Exception {

			Option<A> optA = Utils.<A>headOption(list);

			assertTrue(optA.isSome());
		}

		@Test
		public void tail1共変反変() throws Exception {

			List<A> listA = Utils.<A>tail(list);

			assertThat(listA.get(0).a, is(1));
		}

		@Test
		public void tail2共変反変() throws Exception {

			List<C> list = Arrays.asList(new C(10, 10, 10), new C(20, 20, 20));
			List<B> listA = Utils.<B>tail(list, ArrayList::new);

			assertThat(listA.get(0).a, is(20));
		}

		@Test
		public void zipWithIndex1共変反変() throws Exception {

			List<Tuple2<Integer, A>> l = Utils.<A>zipWithIndex(list);

			assertThat(l.get(0)._1, is(0));
		}

		@Test
		public void zipWithIndex2共変反変() throws Exception {

			List<Tuple2<Integer, A>> l = Utils.<A>zipWithIndex(list, ArrayList::new);

			assertThat(l.get(0)._1, is(0));
		}

		@Test
		public void zipWithIndex3共変反変() throws Exception {

			Stream<Tuple2<Integer, A>> s = Utils.<A>zipWithIndex(stream);

			assertThat(s.collect(Collectors.toList()).get(0)._1, is(0));
		}

		@Test
		public void zipWith共変反変() throws Exception {

			BiFunction<A, A, C> zipper = (a1, a2) -> new C(a1.a, a1.a, a2.a);

			Stream<B> sb = Arrays.asList(new B(1, 1), new B(2, 2)).stream();
			Stream<C> sc = Arrays.asList(new C(10, 10, 20), new C(20, 20, 20)).stream();

			Stream<B> zipped = Utils.<A, A, B>zipWith(sb, sc, zipper);

			List<B> collect = zipped.collect(Collectors.toList());

			assertThat(collect.get(0).a, is(1));
			assertThat(collect.get(0).b, is(1));
		}

		@Test
		public void zip1共変反変() throws Exception {

			List<B> sb = Arrays.asList(new B(1, 1), new B(2, 2));
			List<C> sc = Arrays.asList(new C(10, 10, 10), new C(20, 20, 20));

			List<Tuple2<A, A>> zipped = Utils.<A, A>zip(sb, sc);

			assertThat(zipped.get(0)._1.a, is(1));
			assertThat(zipped.get(0)._2.a, is(10));
		}

		@Test
		public void zip2共変反変() throws Exception {

			List<C> sc = Arrays.asList(new C(1, 1, 1), new C(2, 2, 2));
			List<D> sd = Arrays.asList(new D(10, 10, 10, 10), new D(20, 20, 20, 20));

			List<Tuple2<B, C>> zipped = Utils.<B, C>zip(sc, sd, ArrayList::new);

			assertThat(zipped.get(0)._1.a, is(1));
			assertThat(zipped.get(0)._2.a, is(10));
		}

		@Test
		public void zip3共変反変() throws Exception {

			Stream<B> sb = Arrays.asList(new B(1, 1), new B(2, 2)).stream();
			Stream<C> sc = Arrays.asList(new C(10, 10, 10), new C(20, 20, 20)).stream();

			Stream<Tuple2<A, B>> zipped = Utils.<A, B>zip(sb, sc);

			List<Tuple2<A, B>> collect = zipped.collect(Collectors.toList());
			assertThat(collect.get(0)._1.a, is(1));
			assertThat(collect.get(0)._2.b, is(10));
		}
	}


	@RunWith(JUnit4.class)
	public static class マップに要素が存在しない時 {

		Map<String, Integer> map;

		@Before
		public void setup() {
			this.map = new HashMap<>();
		}

		@Test
		public void getすると値がNoneで取得できる() throws Exception {

			Option<Integer> option = get(map, "key");
			assertTrue(option.isNone());
		}
	}


	@RunWith(JUnit4.class)
	public static class マップに要素が存在する時 {

		Map<String, Integer> map;

		@Before
		public void setup() {
			this.map = new HashMap<>();
			map.put("key1", 1);
			map.put("key2", 2);
			map.put("key3", 3);
		}

		@Test
		public void getすると値をくるんだSomeが取得できる() throws Exception {

			Option<Integer> option = get(map, "key2");
			assertTrue(option.isSome());
			assertThat(option.getOrElse(() -> -1), is(2));
		}

		@Test
		public void keyValueのstreamを取得できる() throws Exception {

			List<String> result = keyValueStream(map)
				.map(keyValue -> keyValue._1 + "-" + keyValue._2)
				.collect(Collectors.toList());

			assertThat(result, is(
				Arrays.asList("key1-1", "key2-2", "key3-3")
			));
		}


		@Test
		public void keyValueの並列streamを取得できる() throws Exception {

			List<String> result = keyValuePStream(map)
				.map(keyValue -> keyValue._1+"-"+keyValue._2)
				.collect(Collectors.toList());

			assertThat(result, hasItems("key1-1", "key2-2", "key3-3"));
		}
	}


	@RunWith(JUnit4.class)
	public static class マップ共通 {

		@Test
		public void get共変反変() throws Exception{

			Map<B, C> map = new HashMap<>();

			Option<A> optA = Utils.<C, A>get(map, new C(0, 0, 0));

			assertTrue(optA.isNone());
		}

		@Test
		public void keyValueStream共変反変() throws Exception {

			Map<B, C> map = new HashMap<>();
			Stream<Tuple2<A, B>> s = Utils.<A, B>keyValueStream(map);

			assertThat(s.collect(Collectors.toList()), hasSize(0));
		}

		@Test
		public void keyValuePStream共変反変() throws Exception {

			Map<B, C> map = new HashMap<>();
			Stream<Tuple2<A, B>> s = Utils.<A, B>keyValuePStream(map);

			assertThat(s.collect(Collectors.toList()), hasSize(0));
		}
	}


	@RunWith(JUnit4.class)
	public static class 何らかのIterable {

		Iterable<String> it;

		@Before
		public void setUp() throws Exception {
			this.it = new HashSet<String>(){{
				add("a");
				add("b");
			}};
		}

		@Test
		public void streamに変換できる() throws Exception {

			Stream<String> stream = stream(it);

			List<String> actual = stream.collect(Collectors.toList());
			assertThat(actual, hasItem("a"));
			assertThat(actual, hasItem("b"));
		}

		@Test
		public void stream共変反変() throws Exception {

			Iterable<B> it = Arrays.asList(new B(0, 0), new B(1, 1));

			Stream<A> stream = Utils.<A>stream(it);

			assertThat(stream.collect(Collectors.toList()).get(0).a, is(0));
		}

		@Test
		public void pstream共変反変() throws Exception {

			Iterable<B> it = Arrays.asList(new B(0, 0), new B(1, 1));

			Stream<A> stream = Utils.<A>pstream(it);

			assertThat(stream.collect(Collectors.toList()).get(0).a, is(0));
		}

		@Test
		public void parallelStreamに変換できる() throws Exception {

			Stream<String> stream = pstream(it);

			assertTrue(stream.isParallel());
			List<String> actual = stream.collect(Collectors.toList());
			assertThat(actual, hasItem("a"));
			assertThat(actual, hasItem("b"));
		}
	}
}