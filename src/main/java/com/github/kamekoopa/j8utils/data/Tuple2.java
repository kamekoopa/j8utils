package com.github.kamekoopa.j8utils.data;

public final class Tuple2<A, B> {

	public final A _1;
	public final B _2;

	protected Tuple2(A _1, B _2) {
		this._1 = _1;
		this._2 = _2;
	}

	public static <A, B> Tuple2<A, B> of(A _1, B _2){
		return new Tuple2<>(_1, _2);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tuple2)) return false;

		Tuple2 tuple2 = (Tuple2) o;
		return _1.equals(tuple2._1) && _2.equals(tuple2._2);

	}

	@Override
	public int hashCode() {
		int result = _1.hashCode();
		result = 31 * result + _2.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "("+_1+", "+_2+")";
	}
}
