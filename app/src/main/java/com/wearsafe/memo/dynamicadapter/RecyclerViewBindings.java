package com.wearsafe.memo.dynamicadapter;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.wearsafe.memo.dynamicadapter.handlers.ClickHandler;
import com.wearsafe.memo.dynamicadapter.handlers.ItemBinder;
import com.wearsafe.memo.dynamicadapter.handlers.ItemTouchHandler;
import com.wearsafe.memo.dynamicadapter.handlers.LongClickHandler;
import com.wearsafe.memo.dynamicadapter.listeners.ItemTouchHelperCallback;
import com.wearsafe.memo.dynamicadapter.listeners.OnItemTouchListener;

import java.util.List;

/**
 * Created by Ali on 09-Dec-17.
 */

public class RecyclerViewBindings {
    private static final int DATA_KEY = 101;
    private static final int CLICK_HANDLER_KEY = 102;
    private static final int LONG_CLICK_HANDLER = 103;
    private static final int ITEM_TOUCH_HANDLER = 104;

    @BindingAdapter("itemBinder")
    public static <T> void setItemBinder(RecyclerView recyclerView, ItemBinder itemBinder){
        DataBindingRVAdapter<T> adapter = new DataBindingRVAdapter(itemBinder,(List<T>) recyclerView.getTag(DATA_KEY));

        ClickHandler<T> clickHandler = (ClickHandler<T>) recyclerView.getTag(CLICK_HANDLER_KEY);
        LongClickHandler<T> longClickHandler = (LongClickHandler<T>) recyclerView.getTag(LONG_CLICK_HANDLER);
        ItemTouchHandler<T> itemTouchHandler = (ItemTouchHandler<T>) recyclerView.getTag(ITEM_TOUCH_HANDLER);

        if(clickHandler!=null)
            adapter.setClickHandler(clickHandler);

        if (longClickHandler!=null)
            adapter.setLongClickHandler(longClickHandler);

        if (itemTouchHandler!=null) {
            adapter.setItemTouchHandler(itemTouchHandler);
            ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter("clickHandler")
    public static <T> void setClickHandler(RecyclerView recyclerView, ClickHandler<T> clickHandler){
        DataBindingRVAdapter<T> adapter = (DataBindingRVAdapter<T>) recyclerView.getAdapter();
        if(adapter==null)
            recyclerView.setTag(CLICK_HANDLER_KEY,clickHandler);
        else
            adapter.setClickHandler(clickHandler);
    }

    @BindingAdapter("longClickHandler")
    public static <T> void setLongClickHandler(RecyclerView recyclerView, LongClickHandler<T> longClickHandler){
        DataBindingRVAdapter<T> adapter = (DataBindingRVAdapter<T>) recyclerView.getAdapter();
        if(adapter==null)
            recyclerView.setTag(LONG_CLICK_HANDLER,longClickHandler);
        else
            adapter.setLongClickHandler(longClickHandler);
    }

    @BindingAdapter("itemTouchHandler")
    public static <T> void setItemTouchHandler(RecyclerView recyclerView, ItemTouchHandler<T> itemTouchHandler){
        DataBindingRVAdapter<T> adapter = (DataBindingRVAdapter<T>) recyclerView.getAdapter();
        if(adapter==null)
            recyclerView.setTag(ITEM_TOUCH_HANDLER,itemTouchHandler);
        else {
            adapter.setItemTouchHandler(itemTouchHandler);
            ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    @BindingAdapter("items")
    public static <T> void setItems(RecyclerView recyclerView, List<T> items){
        DataBindingRVAdapter<T> adapter = (DataBindingRVAdapter<T>) recyclerView.getAdapter();
        if(adapter==null)
            recyclerView.setTag(DATA_KEY,items);
        else
            adapter.setItems(items);
    }
}
