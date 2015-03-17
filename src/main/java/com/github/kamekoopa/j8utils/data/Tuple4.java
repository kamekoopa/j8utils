/*
 * Copyright 2015 kamekoopa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kamekoopa.j8utils.data;

public final class Tuple4<A, B, C, D> {

	public final A _1;
	public final B _2;
	public final C _3;
	public final D _4;

	private Tuple4(A _1, B _2, C _3, D _4) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
		this._4 = _4;
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> of(A _1, B _2, C _3, D _4){
		return new Tuple4<>(_1, _2, _3, _4);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tuple4)) return false;

		Tuple4 tuple3 = (Tuple4) o;

		return _1.equals(tuple3._1) && _2.equals(tuple3._2) && _3.equals(tuple3._3) && _4.equals(tuple3._4);
	}

	@Override
	public int hashCode() {
		int result = _1.hashCode();
		result = 31 * result + _2.hashCode();
		result = 31 * result + _3.hashCode();
		result = 31 * result + _4.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "("+_1+", "+_2+", "+_3+", "+_4+")";
	}
}
