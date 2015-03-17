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

import com.github.kamekoopa.j8utils.utils.*;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Try<A> {

	public static <A> Try<A> of(SE<A> supplier){
		try {
			return new Success<>(supplier.gete());
		}catch (Throwable e){
			return new Failure<>(e);
		}
	}

	public static <A> Success<A> success(A a){
		return new Success<>(a);
	}

	public static <A> Failure<A> failure(Throwable e){
		return new Failure<>(e);
	}

	public abstract <B> Try<B> map(Function<A, B> f);

	public abstract <B> Try<B> mape(FE1<A, B> f) throws Exception;

	public abstract <B> Try<B> failableMap(FE1<A, B> f);

	public abstract <B> Try<B> flatMap(Function<A, Try<B>> f);

	public abstract Option<A> toOption();

	public abstract Either<Throwable, A> toEither();

	public abstract <B> B fold(Function<A, B> success, Function<Throwable, B> failure);

	public abstract A fallback(Function<Throwable, A> f);

	public abstract boolean isSuccess();

	public abstract boolean isFailure();

	public <B, X> Try<X> ap(Try<B> ob, BiFunction<A, B, X> f) {
		return this.flatMap(a -> ob.map(b -> f.apply(a, b)));
	}

	public <B, C, X> Try<X> ap(Try<B> ob, Try<C> oc, F3<A, B, C, X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.map(c -> f.apply(a, b, c))));
	}

	public <B, C, D, X> Try<X> ap(Try<B> ob, Try<C> oc, Try<D> od, F4<A, B, C, D, X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.flatMap(c -> od.map(d -> f.apply(a, b, c, d)))));
	}

	public <B, C, D, E, X> Try<X> ap(Try<B> ob, Try<C> oc, Try<D> od, Try<E> oe, F5<A, B, C, D, E, X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.flatMap(c -> od.flatMap(d -> oe.map( e->f.apply(a, b, c, d, e))))));
	}


	public static final class Success<A> extends Try<A> {

		private final A a;

		private Success(A a) {
			this.a = a;
		}

		@Override
		public Option<A> toOption() {
			return Option.of(a);
		}

		@Override
		public Either<Throwable, A> toEither() {
			return Either.right(a);
		}

		@Override
		public <B> Try<B> map(Function<A, B> f) {
			return new Success<>(f.apply(a));
		}

		@Override
		public <B> Try<B> mape(FE1<A, B> f) throws Exception {
			return new Success<>(f.applye(a));
		}

		@Override
		public <B> Try<B> failableMap(FE1<A, B> f) {
			return Try.of(() -> f.applye(a));
		}

		@Override
		public <B> Try<B> flatMap(Function<A, Try<B>> f) {
			return f.apply(a);
		}

		@Override
		public <B> B fold(Function<A, B> success, Function<Throwable, B> failure) {
			return success.apply(a);
		}

		@Override
		public A fallback(Function<Throwable, A> f) {
			return a;
		}

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isFailure() {
			return false;
		}
	}


	public static final class Failure<A> extends Try<A> {

		private final Throwable e;

		private Failure(Throwable e) {
			this.e = e;
		}

		@Override
		public Option<A> toOption() {
			return Option.none();
		}

		@Override
		public Either<Throwable, A> toEither() {
			return Either.left(e);
		}

		@Override
		public <B> Try<B> map(Function<A, B> f) {
			return new Failure<>(e);
		}

		@Override
		public <B> Try<B> mape(FE1<A, B> f) throws Exception {
			return new Failure<>(e);
		}

		@Override
		public <B> Try<B> failableMap(FE1<A, B> f) {
			return new Failure<>(e);
		}

		@Override
		public <B> Try<B> flatMap(Function<A, Try<B>> f) {
			return new Failure<>(e);
		}

		@Override
		public <B> B fold(Function<A, B> success, Function<Throwable, B> failure) {
			return failure.apply(e);
		}

		@Override
		public A fallback(Function<Throwable, A> f) {
			return f.apply(e);
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isFailure() {
			return true;
		}
	}
}
