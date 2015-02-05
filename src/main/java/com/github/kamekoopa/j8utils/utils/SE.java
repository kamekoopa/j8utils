package com.github.kamekoopa.j8utils.utils;

import java.util.function.Supplier;

@FunctionalInterface
public interface SE<T> extends Supplier<T> {

	@Override
	public default T get() {
		try {
			return gete();
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public T gete() throws Exception;
}
