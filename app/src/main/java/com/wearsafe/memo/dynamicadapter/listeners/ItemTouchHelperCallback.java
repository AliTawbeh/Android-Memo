package com.wearsafe.memo.dynamicadapter.listeners;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Ali on 10-Dec-17.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final OnItemTouchListener mOnItemTouchListener;
    public ItemTouchHelperCallback(OnItemTouchListener touchListener){
        mOnItemTouchListener=touchListener;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //enable left and right swipe directions
        final int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //Disable moving recyclerView's items
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Notify the listener (adapter) when an item is swiped
        mOnItemTouchListener.onItemSwiped(viewHolder.itemView);
    }
}
