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

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(Enclosed.class)
public class EitherTest {

	@RunWith(JUnit4.class)
	public static class 右 {

		Either<String, Integer> either;

		@Before
		public void setup() throws Exception {
			this.either = Either.right(1);
		}

		@Test
		public void 右を表す() throws Exception {
			assertTrue(either.isRight());
		}

		@Test
		public void 左を表さない() throws Exception {
			assertFalse(either.isLeft());
		}

		@Test
		public void mapで値を変更できる() throws Exception {

			Long result = either.map(Date::new)
				.fold(s -> (long) s.length(), Date::getTime);

			assertThat(result, is(1L));
		}

		@Test(expected = Exception.class)
		public void mapeは例外を透過する() throws Exception {
			either.mape(i -> {throw new Exception("error");});
		}

		@Test
		public void flatMapでLeftにできる() throws Exception {

			Either<String, Integer> result = either.flatMap(i -> Either.left(i.toString()));

			assertTrue(result.isLeft());
		}

		@Test(expected = Exception.class)
		public void flatMapeは例外を透過する() throws Exception {
			either.flatMape(i -> {
				throw new Exception();
			});
		}
	}

	@RunWith(JUnit4.class)
	public static class 左 {

		Either<String, Integer> either;

		@Before
		public void setup() throws Exception {
			this.either = Either.left("left");
		}

		@Test
		public void 右を表さない() throws Exception {
			assertFalse(either.isRight());
		}

		@Test
		public void 左を表す() throws Exception {
			assertTrue(either.isLeft());
		}

		@Test
		public void 右優先eitherなのでmapで値を変更できない() throws Exception {

			Long result = either.map(Date::new)
				.fold(s -> (long) s.length(), Date::getTime);

			assertThat(result, is(4L));
		}

		@Test
		public void 右優先eitherなのでmapeの関数は実行されないため何も起きない() throws Exception {
			either.mape(i -> {throw new Exception("error");});
		}

		@Test
		public void 右優先eitherなのでflatMapの関数は実行されないためRightにはできない() throws Exception {

			Either<String, Integer> result = either.flatMap(i -> Either.right(1));

			assertFalse(result.isRight());
		}

		@Test
		public void 右優先eitherなのでflatMapeの関数は実行されないため何も起きない() throws Exception {
			either.flatMape(i -> { throw new Exception(); });
		}
	}
}