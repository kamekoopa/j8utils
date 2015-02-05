package com.github.kamekoopa.j8utils.utils;

import java.util.function.BiFunction;

@FunctionalInterface
public interface FE2<A, B, C> extends BiFunction<A, B, C> {

	@Override
	public default C apply(A a, B b) {
		try {
			return applye(a, b);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public C applye(A A, B b) throws Exception;
}
