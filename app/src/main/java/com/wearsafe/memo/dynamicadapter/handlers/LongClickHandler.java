package com.wearsafe.memo.dynamicadapter.handlers;

/**
 * Created by Ali on 08-Dec-17.
 */

/**
 * Interface to handle when an item {@Link T} is clicked.
 * @param <T> model of the item that must be sent in the long click event
 */
public interface LongClickHandler<T> {
    void onLongClick(T item);
}