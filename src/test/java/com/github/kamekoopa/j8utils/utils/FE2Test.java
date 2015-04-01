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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(JUnit4.class)
public class FE2Test {

	FE2<String, String, String> f;
	FE2<String, String, String> fe;

	@Before
	public void setUp() throws Exception {

		this.f = (s1, s2) -> s1 + s2;
		this.fe = (s1, s2) -> { throw new Exception("error"); };
	}

	@Test
	public void biFunctionから構築できる() throws Exception {

		FE2<String, String, String> fe2 = FE2.from((s1, s2) -> s1 + s2);

		assertThat(fe2.apply("a", "b"), is("ab"));
	}

	@Test
	public void flipできる() throws Exception {

		FE2<String, String, String> flip = this.f.flip();

		assertThat(f.apply("a", "b"), is(flip.apply("b", "a")));
	}

	@Test
	public void カリー化できる() throws Exception {

		FE1<String, FE1<String, String>> curried = f.curried();

		assertThat(curried.apply("a").apply("b"), is("ab"));
	}

	@Test
	public void 二関数として使える() throws Exception {

		assertThat(f.apply("a","b"), is("ab"));
	}

	@Test(expected = Exception.class)
	public void applyeは例外をスローできる() throws Exception {

		fe.apply("a", "b");
	}
}