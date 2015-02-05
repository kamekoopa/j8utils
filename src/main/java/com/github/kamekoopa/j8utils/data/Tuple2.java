package com.github.kamekoopa.j8utils.data;

public class Tuple2<A, B> {

	public final A _1;
	public final B _2;

	protected Tuple2(A _1, B _2) {
		this._1 = _1;
		this._2 = _2;
	}

	public static <A, B> Tuple2<A, B> of(A _1, B _2){
		return new Tuple2<>(_1, _2);
	}
}
