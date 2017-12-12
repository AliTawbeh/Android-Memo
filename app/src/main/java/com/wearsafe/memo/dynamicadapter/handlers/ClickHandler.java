package com.wearsafe.memo.dynamicadapter.handlers;

/**
 * Created by Ali on 08-Dec-17.
 */

/**
 * Interface to handle when an item {@Link T} is clicked.
 * @param <T> model of the item that must be sent in the click event
 */
public interface ClickHandler<T> {
    void onClick(T item);
}
