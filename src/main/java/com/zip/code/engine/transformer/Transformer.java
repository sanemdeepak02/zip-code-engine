package com.zip.code.engine.transformer;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@FunctionalInterface
public interface Transformer<F, T> {

    T transform(F from);
}
