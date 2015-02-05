package com.github.kamekoopa.j8utils.data;

import com.github.kamekoopa.j8utils.utils.FE1;
import com.github.kamekoopa.j8utils.utils.SE;

import java.util.function.Function;

public abstract class Try<A> {

	public static <A> Try<A> of(SE<A> supplier){
		try {
			return new Success<>(supplier.gete());
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

	public abstract <B> Try<B> map(Function<A, B> f);

	public abstract <B> Try<B> mape(FE1<A, B> f) throws Exception;

	public abstract <B> Try<B> failableMap(FE1<A, B> f);

	public abstract <B> Try<B> flatMap(Function<A, Try<B>> f);

	public abstract Option<A> toOption();

	public abstract Either<Exception, A> toEither();

	public abstract <B> B fold(Function<A, B> success, Function<Exception, B> failure);

	public abstract A fallback(Function<Exception, A> f);



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
		public <B> B fold(Function<A, B> success, Function<Exception, B> failure) {
			return success.apply(a);
		}

		@Override
		public A fallback(Function<Exception, A> f) {
			return a;
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
		public <B> B fold(Function<A, B> success, Function<Exception, B> failure) {
			return failure.apply(e);
		}

		@Override
		public A fallback(Function<Exception, A> f) {
			return f.apply(e);
		}
	}
}
