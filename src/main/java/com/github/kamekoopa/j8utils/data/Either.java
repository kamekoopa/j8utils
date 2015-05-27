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

public abstract class Either<A, B> {

	public static <X, B> Left<X, B> left(X a){
		return new Left<>(a);
	}

	public static <A, X> Right<A, X> right(X b){
		return new Right<>(b);
	}

	public abstract boolean isLeft();

	public abstract boolean isRight();

	public abstract <BB> Either<A, BB> map(Function<? super B, ? extends BB> f);

	public abstract <BB> Either<A, BB> mape(FE1<? super B, ? extends BB> f) throws Exception;

	public abstract <BB> Either<A, BB> flatMap(Function<? super B, Either<A, BB>> f);

	public abstract <BB> Either<A, BB> flatMape(FE1<? super B, Either<A, BB>> f) throws Exception;

	public abstract <C> C fold(Function<? super A, ? extends C> fl, Function<? super B, ? extends C> fr);


	public static class Left<A, B> extends Either<A, B> {

		private final A a;

		private Left(A a){
			this.a = a;
		}

		@Override
		public boolean isLeft() {
			return true;
		}

		@Override
		public boolean isRight() {
			return false;
		}

		@Override
		public <BB> Either<A, BB> map(Function<? super B, ? extends BB> f) {
			return new Left<>(a);
		}

		@Override
		public <BB> Either<A, BB> mape(FE1<? super B, ? extends BB> f) throws Exception {
			return new Left<>(a);
		}

		@Override
		public <BB> Either<A, BB> flatMap(Function<? super B, Either<A, BB>> f) {
			return new Left<>(a);
		}

		@Override
		public <BB> Either<A, BB> flatMape(FE1<? super B, Either<A, BB>> f) throws Exception {
			return new Left<>(a);
		}

		@Override
		public <C> C fold(Function<? super A, ? extends C> fl, Function<? super B, ? extends C> fr) {
			return fl.apply(a);
		}
	}

	public static class Right<A, B> extends Either<A,B> {

		private final B b;

		private Right(B b){
			this.b = b;
		}

		@Override
		public boolean isLeft() {
			return false;
		}

		@Override
		public boolean isRight() {
			return true;
		}

		@Override
		public <BB> Either<A, BB> map(Function<? super B, ? extends BB> f) {
			return new Right<>(f.apply(b));
		}

		@Override
		public <BB> Either<A, BB> mape(FE1<? super B, ? extends BB> f) throws Exception {
			return new Right<>(f.apply(b));
		}

		@Override
		public <BB> Either<A, BB> flatMap(Function<? super B, Either<A, BB>> f) {
			return f.apply(b);
		}

		@Override
		public <BB> Either<A, BB> flatMape(FE1<? super B, Either<A, BB>> f) throws Exception {
			return f.apply(b);
		}

		@Override
		public <C> C fold(Function<? super A, ? extends C> fl, Function<? super B, ? extends C> fr) {
			return fr.apply(b);
		}
	}
}
