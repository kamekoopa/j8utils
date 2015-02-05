package com.github.kamekoopa.j8utils.data;

import com.github.kamekoopa.j8utils.utils.SE;

public class LazyVal<A> {

	private final SE<A> supplier;
	private A cache = null;

	private LazyVal(SE<A> supplier){
		this.supplier = supplier;
	}

	public static <A> LazyVal<A> of(SE<A> supplier){
		return new LazyVal<>(supplier);
	}

	public A get() {

		if(cache == null){
			this.cache = supplier.get();
		}

		return cache;
	}

	public A gete() throws Exception {

		if(cache == null){
			this.cache = supplier.gete();
		}

		return cache;
	}
}
