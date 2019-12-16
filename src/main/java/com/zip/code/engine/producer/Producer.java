package com.zip.code.engine.producer;

/**
 * Created by sanemdeepak on 12/15/19.
 */
public interface Producer<R, V> {
    R produce(V v);
    R produce(V v, String destination);
}
