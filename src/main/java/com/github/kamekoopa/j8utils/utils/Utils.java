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

	@SuppressWarnings({"unchecked"})
	public static <A> Stream<A> stream(Iterable<? extends A> iterable) {
		return StreamSupport.stream((Spliterator<A>)iterable.spliterator(), false);
	}

	@SuppressWarnings({"unchecked"})
	public static <A> Stream<A> pstream(Iterable<? extends A> iterable) {
		return StreamSupport.stream((Spliterator<A>)iterable.spliterator(), true);
	}

	public static <A> Option<A> get(List<? extends A> list, int i){
		return Try.<A>of(() -> list.get(i)).toOption();
	}

	public static <K, V> Option<V> get(Map<? super K, ? extends V> map, K key){
		return Try.<V>of(() -> map.get(key)).toOption();
	}

	public static <K, V> Stream<Tuple2<K, V>> keyValueStream(Map<? extends K, ? extends V> map) {
		return mapper(map.entrySet().stream());
	}

	public static <K, V> Stream<Tuple2<K, V>> keyValuePStream(Map<? extends K, ? extends V> map) {
		return mapper(map.entrySet().parallelStream());
	}

	private static <K, V> Stream<Tuple2<K, V>> mapper(Stream<? extends Map.Entry<? extends K, ? extends V>> stream) {
		return stream
			.map(entry -> Tuple2.of(entry.getKey(), entry.getValue()));
	}

	public static <A> A head(List<? extends A> list){
		try {
			return list.get(0);
		}catch (IndexOutOfBoundsException e){
			throw new NoSuchElementException();
		}
	}

	public static <A> Option<A> headOption(List<? extends A> list) {
		return get(list, 0);
	}

	public static <A> List<A> tail(List<? extends A> list){
		return tail(list, ArrayList::new);
	}

	public static <A> List<A> tail(List<? extends A> list, Supplier<? extends List<A>> listImpl){

		if(list.isEmpty()) {
			throw new NoSuchElementException();
		}

		List<A> result = listImpl.get();
		for (int i = 1; i < list.size(); i++) {
			result.add(list.get(i));
		}
		return result;
	}

	public static <A> List<Tuple2<Integer, A>> zipWithIndex(List<? extends A> list) {
		Supplier<List<Tuple2<Integer, A>>> s = ArrayList::new;
		return zipWithIndex(list, s);
	}

	public static <A> List<Tuple2<Integer, A>> zipWithIndex(List<? extends A> list, Supplier<? extends List<Tuple2<Integer, A>>> listImpl) {

		List<Integer> indexes = IntStream.range(0, list.size())
			.mapToObj(Integer::valueOf)
			.collect(Collectors.toList());

		return zip(indexes, list, listImpl);
	}

	public static <A, B> List<Tuple2<A, B>> zip(List<? extends A> list1, List<? extends B> list2){
		Supplier<List<Tuple2<A, B>>> s = ArrayList::new;
		return zip(list1, list2, s);
	}

	public static <A, B> List<Tuple2<A, B>> zip(List<? extends A> list1, List<? extends B> list2, Supplier<? extends List<Tuple2<A, B>>> listImpl) {

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

	public static <A> Stream<Tuple2<Integer, A>> zipWithIndex(Stream<? extends A> stream1){
		return zip(Stream.iterate(0, i -> i + 1), stream1);
	}

	public static <A, B> Stream<Tuple2<A, B>> zip(Stream<? extends A> stream1, Stream<? extends B> stream2){
		return zipWith(stream1, stream2, Tuple2::of);
	}

	public static <A, B, C> Stream<C> zipWith(Stream<? extends A> stream1, Stream<? extends B> stream2, BiFunction<? super A, ? super B, ? extends C> f){

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
		return optionStream
			.flatMap(Option::stream);
	}

	private static class Zipped<A, B, C> implements Spliterator<C> {

		private final Spliterator<? extends A> a;
		private final Spliterator<? extends B> b;
		private final BiFunction<? super A, ? super B, ? extends C> f;
		private final int characteristics;
		private final long estimateSize;

		public Zipped(Spliterator<? extends A> a, Spliterator<? extends B> b, BiFunction<? super A, ? super B, ? extends C> f) {
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

			Spliterator<? extends A> a = this.a.trySplit();
			Spliterator<? extends B> b = this.b.trySplit();

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
