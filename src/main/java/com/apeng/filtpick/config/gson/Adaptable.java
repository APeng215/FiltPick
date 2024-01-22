package com.apeng.filtpick.config.gson;

import com.google.gson.TypeAdapter;


/**
 * Typically, the generic T should be the class that implements the interface.
 * @param <T> the class that implements the interface
 */
@FunctionalInterface
public interface Adaptable<T> {
    TypeAdapter<T> getTypeAdapter();
}
