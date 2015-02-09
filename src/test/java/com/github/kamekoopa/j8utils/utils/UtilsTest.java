package com.github.kamekoopa.j8utils.utils;


import com.github.kamekoopa.j8utils.data.Option;
import com.github.kamekoopa.j8utils.data.Tuple2;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.kamekoopa.j8utils.utils.Utils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
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
	}
}