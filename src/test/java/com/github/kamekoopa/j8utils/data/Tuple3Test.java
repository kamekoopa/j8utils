package com.github.kamekoopa.j8utils.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class Tuple3Test {

	@Test
	public void 各要素が等しければ等しい() throws Exception {

		Tuple3<String, Integer, List<Integer>> t1 = Tuple3.of("one", 2, Arrays.asList(3));
		Tuple3<String, Integer, List<Integer>> t2 = Tuple3.of("one", 2, Arrays.asList(3));

		assertThat(t1, is(t2));
	}

	@Test
	public void 一つでも要素が等しくなければ等しくない() throws Exception {

		Tuple3<String, Integer, List<Integer>> t1 = Tuple3.of("one", 2, Arrays.asList(3));
		Tuple3<String, Integer, List<Integer>> t2 = Tuple3.of("one", 2, Arrays.asList(4));

		assertThat(t1, is(not(t2)));
	}
}