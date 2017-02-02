package com.inqbarna.bintree;

import java.util.List;

/**
 * @author David García <david.garcia@inqbarna.com>
 * @version 1.0 31/1/17
 */

public interface GroupItem<T> extends Expandable<T> {
    T dataContent();

    @Override
    List<? extends GroupItem<T>> childBlocks();
}
