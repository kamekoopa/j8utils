package com.github.kamekoopa.j8utils.data;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
}