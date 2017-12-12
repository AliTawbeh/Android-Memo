package com.wearsafe.memo.dynamicadapter.listeners;

/**
 * Created by Ali on 09-Dec-17.
 */

import android.view.View;

/**
 * Interface to listen to item swipe
 * @param <T> model of the item that must be sent in the swipe event
 */
public interface OnItemTouchListener<T> {
    boolean onItemSwiped(View view);
}
