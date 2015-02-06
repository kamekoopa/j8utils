package com.github.kamekoopa.j8utils.data;

public final class Tuple3<A, B, C> {

	public final A _1;
	public final B _2;
	public final C _3;

	private Tuple3(A _1, B _2, C _3) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
	}

	public static <A, B, C> Tuple3<A, B, C> of(A _1, B _2, C _3){
		return new Tuple3<>(_1, _2, _3);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tuple3)) return false;

		Tuple3 tuple3 = (Tuple3) o;

		return _1.equals(tuple3._1) && _2.equals(tuple3._2) && _3.equals(tuple3._3);
	}

	@Override
	public int hashCode() {
		int result = _1.hashCode();
		result = 31 * result + _2.hashCode();
		result = 31 * result + _3.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "("+_1+", "+_2+", "+_3+")";
	}
}
