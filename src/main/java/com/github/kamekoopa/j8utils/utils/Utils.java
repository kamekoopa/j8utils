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

package com.github.kamekoopa.j8utils.utils;

import com.github.kamekoopa.j8utils.data.Option;
import com.github.kamekoopa.j8utils.data.Try;
import com.github.kamekoopa.j8utils.data.Tuple2;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Utils {

	private Utils(){}

	public static <A> Option<A> get(List<A> list, int i){
		return Try.of(() -> list.get(i)).toOption();
	}

	public static <K, V> Option<V> get(Map<K, V> map, K key){
		return Try.of(() -> map.get(key)).toOption();
	}

	public static <K, V> Stream<Tuple2<K, V>> keyValueStream(Map<K, V> map) {
		return mapper(map.entrySet().stream());
	}

	public static <K, V> Stream<Tuple2<K, V>> keyValuePStream(Map<K, V> map) {
		return mapper(map.entrySet().parallelStream());
	}

	private static <K, V> Stream<Tuple2<K, V>> mapper(Stream<Map.Entry<K, V>> stream) {
		return stream
			.map(entry -> Tuple2.of(entry.getKey(), entry.getValue()));
	}

	public static <A> A head(List<A> list){
		try {
			return list.get(0);
		}catch (IndexOutOfBoundsException e){
			throw new NoSuchElementException();
		}
	}

	public static <A> Option<A> headOption(List<A> list) {
		return get(list, 0);
	}

	public static <A> List<A> tail(List<A> list){
		return tail(list, ArrayList::new);
	}

	public static <A> List<A> tail(List<A> list, Supplier<? extends List<A>> listImpl){

		if(list.isEmpty()) {
			throw new NoSuchElementException();
		}

		List<A> result = listImpl.get();
		for (int i = 1; i < list.size(); i++) {
			result.add(list.get(i));
		}
		return result;
	}

	public static <A> List<Tuple2<Integer, A>> zipWithIndex(List<A> list) {
		Supplier<List<Tuple2<Integer, A>>> s = ArrayList::new;
		return zipWithIndex(list, s);
	}

	public static <A> List<Tuple2<Integer, A>> zipWithIndex(List<A> list, Supplier<List<Tuple2<Integer, A>>> listImpl) {

		List<Integer> indexes = IntStream.range(0, list.size())
			.mapToObj(Integer::valueOf)
			.collect(Collectors.toList());

		return zip(indexes, list, listImpl);
	}

	public static <A, B> List<Tuple2<A, B>> zip(List<A> list1, List<B> list2){
		Supplier<List<Tuple2<A, B>>> s = ArrayList::new;
		return zip(list1, list2, s);
	}

	public static <A, B> List<Tuple2<A, B>> zip(List<A> list1, List<B> list2, Supplier<List<Tuple2<A, B>>> listImpl) {

		List<Tuple2<A, B>> list = listImpl.get();
		if(list1.size() > list2.size()){

			for (int i = 0; i < list2.size(); i++) {
				list.add(Tuple2.of(list1.get(i), list2.get(i)));
			}
		}else{
			for (int i = 0; i < list1.size(); i++) {
				list.add(Tuple2.of(list1.get(i), list2.get(i)));
			}
		}

		return list;
	}

	public static <A> Stream<Tuple2<Integer, A>> zipWithIndex(Stream<A> stream1){
		return zip(Stream.iterate(0, i -> i + 1), stream1);
	}

	public static <A, B> Stream<Tuple2<A, B>> zip(Stream<A> stream1, Stream<B> stream2){
		return zipWith(stream1, stream2, Tuple2::of);
	}

	public static <A, B, C> Stream<C> zipWith(Stream<A> stream1, Stream<B> stream2, BiFunction<A, B, C> f){

		Zipped<A, B, C> zipped = new Zipped<>(stream1.spliterator(), stream2.spliterator(), f);
		return StreamSupport.stream(() -> zipped, zipped.characteristics(), false);
	}

	public static <T> List<T> filterSome(List<Option<T>> optionList){
		return filterSome(optionList.stream());
	}

	public static <T> List<T> filterSome(Stream<Option<T>> optionStream){
		return filterSomeToStream(optionStream).collect(Collectors.toList());
	}

	public static <T> Stream<T> filterSomeToStream(Stream<Option<T>> optionStream){
		return optionStream.filter(Option::isSome)
			.flatMap(Option::stream);
	}

	private static class Zipped<A, B, C> implements Spliterator<C> {

		private final Spliterator<A> a;
		private final Spliterator<B> b;
		private final BiFunction<A, B, C> f;
		private final int characteristics;
		private final long estimateSize;

		public Zipped(Spliterator<A> a, Spliterator<B> b, BiFunction<A, B, C> f) {
			this.a = Objects.requireNonNull(a);
			this.b = Objects.requireNonNull(b);
			this.f = f;
			this.characteristics = a.characteristics() & b.characteristics();
			this.estimateSize = Math.min(a.estimateSize(), b.estimateSize());
		}

		@Override
		public boolean tryAdvance(Consumer<? super C> action) {

			boolean[] bHasRemaining = { false };
			boolean aHasRemaining = this.a.tryAdvance(_a ->
				bHasRemaining[0] = this.b.tryAdvance(_b ->
					action.accept(f.apply(_a, _b))
				)
			);

			return aHasRemaining && bHasRemaining[0];
		}

		@Override
		public Spliterator<C> trySplit() {

			Spliterator<A> a = this.a.trySplit();
			Spliterator<B> b = this.b.trySplit();

			if(a == null || b == null) {
				return null;
			}else{
				return new Zipped<>(a, b, f);
			}
		}

		@Override
		public long estimateSize() {
			return estimateSize;
		}

		@Override
		public int characteristics() {
			return characteristics;
		}
	}
}
