package com.wearsafe.memo.dynamicadapter.handlers;

/**
 * Created by Ali on 08-Dec-17.
 */

/**
 * Interface to handle when an item {@Link T} is swiped.
 * @param <T> model of the item that must be sent in the swipe event
 */
public interface ItemTouchHandler<T> {
    void onItemSwiped(T item);
}
