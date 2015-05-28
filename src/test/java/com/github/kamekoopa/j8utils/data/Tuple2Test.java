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


import com.github.kamekoopa.j8utils.test.Tools.A;
import com.github.kamekoopa.j8utils.test.Tools.B;
import com.github.kamekoopa.j8utils.test.Tools.C;
import com.github.kamekoopa.j8utils.utils.FE1;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class Tuple2Test {

	@Test
	public void 各要素が等しければ等しい() throws Exception {

		Tuple2<String, Integer> t1 = Tuple2.of("one", 2);
		Tuple2<String, Integer> t2 = Tuple2.of("one", 2);

		assertThat(t1, is(t2));
	}

	@Test
	public void 一つでも要素が等しくなければ等しくない() throws Exception {

		Tuple2<String, Integer> t1 = Tuple2.of("one", 2);
		Tuple2<String, Integer> t2 = Tuple2.of("one", 1);

		assertThat(t1, is(not(t2)));
	}

	@Test
	public void mod1で左側を変更できる() throws Exception {

		Tuple2<String, Integer> t1 = Tuple2.of("one", 1);

		assertThat(t1.mod1(String::length)._1, is(3));
	}

	@Test(expected = Exception.class)
	public void 例外投げられる版mod1内で起きた例外は透過する() throws Exception {

		Tuple2<String, Integer> t1 = Tuple2.of("one", 1);

		t1.<Integer>mod1e(_1 -> {
			throw new Exception();
		});
	}

	@Test
	public void mod2で右側を変更できる() throws Exception {

		Tuple2<String, Integer> t1 = Tuple2.of("one", 1);

		assertThat(t1.mod2(String::valueOf)._2, is("1"));
	}

	@Test(expected = Exception.class)
	public void 例外投げられる版mod2内で起きた例外は透過する() throws Exception {

		Tuple2<String, String> t1 = Tuple2.of("one", "a");
		t1.<Integer>mod2e(Integer::valueOf);
	}

	@Test
	public void mod1共変反変() throws Exception {

		Tuple2<B, B> t2 = Tuple2.of(new B(1, 2), new B(3, 4));

		Function<A, C> mapper = a -> new C(a.a, 10, 20);
		Tuple2<B, B> mapped = t2.<B>mod1(mapper);

		assertThat(mapped._1.a, is(1));
		assertThat(mapped._1.b, is(10));

	}

	@Test
	public void mod1e共変反変() throws Exception {

		Tuple2<B, B> t2 = Tuple2.of(new B(1, 2), new B(3, 4));

		FE1<A, C> mapper = a -> new C(a.a, 10, 20);
		Tuple2<B, B> mapped = t2.<B>mod1e(mapper);

		assertThat(mapped._1.a, is(1));
		assertThat(mapped._1.b, is(10));

	}

	@Test
	public void mod2共変反変() throws Exception {

		Tuple2<B, B> t2 = Tuple2.of(new B(1, 2), new B(3, 4));

		Function<A, C> mapper = a -> new C(a.a, 10, 20);
		Tuple2<B, B> mapped = t2.<B>mod2(mapper);

		assertThat(mapped._2.a, is(3));
		assertThat(mapped._2.b, is(10));

	}

	@Test
	public void mod2e共変反変() throws Exception {

		Tuple2<B, B> t2 = Tuple2.of(new B(1, 2), new B(3, 4));

		FE1<A, C> mapper = a -> new C(a.a, 10, 20);
		Tuple2<B, B> mapped = t2.<B>mod2e(mapper);

		assertThat(mapped._2.a, is(3));
		assertThat(mapped._2.b, is(10));
	}
}