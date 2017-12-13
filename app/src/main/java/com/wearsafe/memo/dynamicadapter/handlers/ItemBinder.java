package com.wearsafe.memo.dynamicadapter.handlers;

/**
 * Created by Ali on 06-Dec-17.
 */

/**
 * This interface is used to get the variable that will populate a recyclerView's entry along with
 * the correspondent layout.
 */
public interface ItemBinder {

    int getBindingVariable();

    int getLayoutRes();
}
