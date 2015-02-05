package com.github.kamekoopa.j8utils.data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class FutureBuilder {

	private final Option<Executor> executorOpt;

	private FutureBuilder(Option<Executor> executorOpt) {
		this.executorOpt = executorOpt;
	}

	public static FutureBuilder build(){
		return new FutureBuilder(Option.none());
	}

	public static FutureBuilder buildWith(Executor executor) {
		return new FutureBuilder(Option.of(executor));
	}

	public <A> Future<A> run(Supplier<A> supplier) {
		return new Future<>(supplier);
	}

	private <A> Supplier<CompletableFuture<A>> supplyAsync(Supplier<A> supplier){

		return () -> executorOpt.fold(
			LazyVal.of(() -> CompletableFuture.supplyAsync(supplier)),
			executor -> CompletableFuture.supplyAsync(supplier, executor)
		);
	}

	private <A, B> Supplier<CompletableFuture<B>> thenApplyAsync(Function<A, B> f, CompletableFuture<A> future){

		return () -> executorOpt.fold(
			LazyVal.of(() -> future.thenApplyAsync(f)),
			executor -> future.thenApplyAsync(f, executor)
		);
	}

	private <A, B> Supplier<CompletableFuture<B>> thenComposeAsync(Function<A, CompletableFuture<B>> f, CompletableFuture<A> future){

		return () -> executorOpt.fold(
			LazyVal.of(() -> future.thenComposeAsync(f)),
			executor -> future.thenComposeAsync(f, executor)
		);
	}


	public class Future<A> {

		private final CompletableFuture<A> underlying;

		private Future(Supplier<A> supplier) {
			this.underlying = supplyAsync(supplier).get();
		}

		private Future(CompletableFuture<A> underlying) {
			this.underlying = underlying;
		}

		public <B> Future<B> map(Function<A, B> f) {
			return new Future<>(
				thenApplyAsync(f, this.underlying).get()
			);
		}

		public <B> Future<B> flatMap(Function<A, Future<B>> f) {
			return new Future<>(
				thenComposeAsync(
					v -> f.apply(v).underlying,
					this.underlying
				).get()
			);
		}

		public <B> B get(Function<A, B> success, Function<Exception, B> failure){
			return Try.of(this.underlying::get).fold(success, failure);
		}

		public <B> B get(Function<A, B> success, Function<Exception, B> failure, long time, TimeUnit unit){
			return Try.of(() -> this.underlying.get(time, unit)).fold(success, failure);
		}
	}
}
