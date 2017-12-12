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
        final int swipeFlag = ItemTouchHelper.END;
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mOnItemTouchListener.onItemSwiped(viewHolder.itemView);
    }
}
