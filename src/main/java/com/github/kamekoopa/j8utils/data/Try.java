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

	public static <A> Try<A> of(SE<? extends A> supplier){
		try {
			return new Success<>(supplier.get());
		}catch (Exception e){
			return new Failure<>(e);
		}
	}

	public static <A> Success<A> success(A a){
		return new Success<>(a);
	}

	public static <A> Failure<A> failure(Exception e){
		return new Failure<>(e);
	}

	public abstract <B> Try<B> map(Function<? super A, ? extends B> f);

	public abstract <B> Try<B> mape(FE1<? super A, ? extends B> f) throws Exception;

	public abstract <B> Try<B> failableMap(FE1<? super A, ? extends B> f);

	public abstract <B> Try<B> flatMap(Function<? super A, ? extends Try<? extends B>> f);

	public abstract Option<A> toOption();

	public abstract Either<Exception, A> toEither();

	public abstract <B> B fold(Function<? super A, ? extends B> success, Function<Exception, ? extends B> failure);

	public abstract A recover(Function<Exception, ? extends A> f);

	public abstract boolean isSuccess();

	public abstract boolean isFailure();

	public <B, X> Try<X> ap(Try<? extends B> ob, BiFunction<? super A, ? super B, ? extends X> f) {
		return this.flatMap(a -> ob.map(b -> f.apply(a, b)));
	}

	public <B, C, X> Try<X> ap(Try<? extends B> ob, Try<? extends C> oc, F3<? super A, ? super B, ? super C, ? extends X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.map(c -> f.apply(a, b, c))));
	}

	public <B, C, D, X> Try<X> ap(Try<? extends B> ob, Try<? extends C> oc, Try<? extends D> od, F4<? super A, ? super B, ? super C, ? super D, ? extends X> f) {
		return this.flatMap(a -> ob.flatMap(b -> oc.flatMap(c -> od.map(d -> f.apply(a, b, c, d)))));
	}

	public <B, C, D, E, X> Try<X> ap(Try<? extends B> ob, Try<? extends C> oc, Try<? extends D> od, Try<? extends E> oe, F5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends X> f) {
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
		public Either<Exception, A> toEither() {
			return Either.right(a);
		}

		@Override
		public <B> Try<B> map(Function<? super A, ? extends B> f) {
			return new Success<>(f.apply(a));
		}

		@Override
		public <B> Try<B> mape(FE1<? super A, ? extends B> f) throws Exception {
			return new Success<>(f.apply(a));
		}

		@Override
		public <B> Try<B> failableMap(FE1<? super A, ? extends B> f) {
			return Try.of(() -> f.apply(a));
		}

		@Override
		public <B> Try<B> flatMap(Function<? super A, ? extends Try<? extends B>> f) {
			return f.apply(a).map(Function.identity());
		}

		@Override
		public <B> B fold(Function<? super A, ? extends B> success, Function<Exception, ? extends B> failure) {
			return success.apply(a);
		}

		@Override
		public A recover(Function<Exception, ? extends A> f) {
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

		private final Exception e;

		private Failure(Exception e) {
			this.e = e;
		}

		@Override
		public Option<A> toOption() {
			return Option.none();
		}

		@Override
		public Either<Exception, A> toEither() {
			return Either.left(e);
		}

		@Override
		public <B> Try<B> map(Function<? super A, ? extends B> f) {
			return new Failure<>(e);
		}

		@Override
		public <B> Try<B> mape(FE1<? super A, ? extends B> f) throws Exception {
			return new Failure<>(e);
		}

		@Override
		public <B> Try<B> failableMap(FE1<? super A, ? extends B> f) {
			return new Failure<>(e);
		}

		@Override
		public <B> Try<B> flatMap(Function<? super A, ? extends Try<? extends B>> f) {
			return new Failure<>(e);
		}

		@Override
		public <B> B fold(Function<? super A, ? extends B> success, Function<Exception, ? extends B> failure) {
			return failure.apply(e);
		}

		@Override
		public A recover(Function<Exception, ? extends A> f) {
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
