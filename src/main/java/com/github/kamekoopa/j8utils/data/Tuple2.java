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

import com.github.kamekoopa.j8utils.utils.FE1;

import java.util.function.Function;

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

	public <AA> Tuple2<AA, B> mod1(Function<? super A, ? extends AA> f) {
		return Tuple2.of(f.apply(_1), _2);
	}

	public <AA> Tuple2<AA, B> mod1e(FE1<? super A, ? extends AA> f) throws Exception {
		return Tuple2.of(f.apply(_1), _2);
	}

	public <BB> Tuple2<A, BB> mod2(Function<? super B, ? extends BB> f) {
		return Tuple2.of(_1, f.apply(_2));
	}

	public <BB> Tuple2<A, BB> mod2e(FE1<? super B, ? extends BB> f) throws Exception {
		return Tuple2.of(_1, f.apply(_2));
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
