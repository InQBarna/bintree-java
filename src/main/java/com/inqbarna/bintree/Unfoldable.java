package com.inqbarna.bintree;

import java.util.List;

/**
 * @author David García <david.garcia@inqbarna.com>
 * @version 1.0 31/1/17
 */

public interface Unfoldable<T> {
    List<? extends Unfoldable<T>> childBlocks();
}
