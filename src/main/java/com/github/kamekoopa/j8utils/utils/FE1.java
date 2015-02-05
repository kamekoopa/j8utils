package com.github.kamekoopa.j8utils.utils;

import java.util.function.Function;

@FunctionalInterface
public interface FE1<A, B> extends Function<A, B> {

	@Override
	public default B apply(A a) {
		try {
			return applye(a);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public B applye(A A) throws Exception;
}
