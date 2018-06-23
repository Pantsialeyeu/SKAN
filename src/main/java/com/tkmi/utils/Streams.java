package com.tkmi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {
  private static <T> Stream<T> toStream(Spliterator<T> iterator) {
    boolean parallel = false;
    return StreamSupport.stream(iterator, parallel);
  }

  public static <T> Stream<T> toStream(Iterable<T> sourceIterable) {
    return toStream(sourceIterable.iterator(), false);
  }

  public static <T> Stream<T> toStream(Iterator<T> sourceIterator) {
    return toStream(sourceIterator, false);
  }

  public static <T> Stream<T> toStream(Iterator<T> sourceIterator, boolean parallel) {
    Iterable<T> iterable = () -> sourceIterator;
    return StreamSupport.stream(iterable.spliterator(), parallel);
  }

  public static <T> Stream<T> toStream(T[] array) {
    return Stream.of(array);
  }

  public static Stream<JsonNode> toStream(ArrayNode array) {
    return toStream(array.spliterator());
  }

  public static Stream<Map.Entry<String, JsonNode>> toStream(ObjectNode object) {
    return JsonHelper.toMap(object).entrySet().stream();
  }
}
