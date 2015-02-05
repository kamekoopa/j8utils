package com.github.kamekoopa.j8utils.data;

public class Tuple3<A, B, C> extends Tuple2<A, B> {

	public final C _3;

	private Tuple3(A _1, B _2, C _3) {
		super(_1, _2);
		this._3 = _3;
	}

	public static <A, B, C> Tuple3<A, B, C> of(A _1, B _2, C _3){
		return new Tuple3<>(_1, _2, _3);
	}
}
