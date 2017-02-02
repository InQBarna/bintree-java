package com.inqbarna.bintree;

import java.util.List;

/**
 * @author David García <david.garcia@inqbarna.com>
 * @version 1.0 31/1/17
 */

public interface LinearItem<T> extends Unfoldable<T> {
    T dataContent();

    @Override
    List<? extends LinearItem<T>> childBlocks();
}
