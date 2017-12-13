package com.wearsafe.memo.dynamicadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wearsafe.memo.dynamicadapter.handlers.ClickHandler;
import com.wearsafe.memo.dynamicadapter.handlers.ItemBinder;
import com.wearsafe.memo.dynamicadapter.handlers.ItemTouchHandler;
import com.wearsafe.memo.dynamicadapter.handlers.LongClickHandler;
import com.wearsafe.memo.dynamicadapter.listeners.OnItemTouchListener;

import java.util.List;

/**
 * Created by Ali on 04-Dec-17.
 */

public class DataBindingRVAdapter<T> extends RecyclerView.Adapter<DataBindingRVAdapter.DataBindingViewHolder>
implements View.OnClickListener, View.OnLongClickListener, OnItemTouchListener{
    //TAG ID used to attach the item <T> to the viewHolder
    private static final int ITEM_TAG = -124;
    //Observable list of items that populates the recyclerView
    @Nullable
    private ObservableList<T> mItems;
    //The callback that is called by ObservableList when the list has changed.
    @Nullable
    private final CustomOnListChangedCallback<T> mCustomOnListChangedCallback;
    @Nullable
    private LayoutInflater mInflater;
    @Nullable
    private final ItemBinder mItemBinder;
    @Nullable
    private ClickHandler<T> mClickHandler;
    @Nullable
    private LongClickHandler<T> mLongClickHandler;
    @Nullable
    private ItemTouchHandler<T> mItemTouchHandler;

    public DataBindingRVAdapter(@NonNull ItemBinder itemBinder, @Nullable List<T> items){
        mItemBinder = itemBinder;
        mCustomOnListChangedCallback = new CustomOnListChangedCallback<>(this);
        setItems(items);
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mInflater == null)
            mInflater = LayoutInflater.from(parent.getContext());

        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(mInflater,viewType,parent,false);
        return new DataBindingViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder holder, int position) {
        //if the list of items is null or empty do nothing
        if(mItems ==null || mItems.size()==0)
            return;
        //Get the item at the position
        T item = mItems.get(position);
        if(item==null)
            return;
        //Pass the variable item to the view holder, where the viewHolder's layout populates the views
        if(mItemBinder !=null)
            holder.viewDataBinding.setVariable(mItemBinder.getBindingVariable(),item);
        //Attach the item to the viewHolder so it can be used for example when we want to delete it in swipe event
        holder.viewDataBinding.getRoot().setTag(ITEM_TAG,item);
        holder.viewDataBinding.getRoot().setOnClickListener(this);
        holder.viewDataBinding.getRoot().setOnLongClickListener(this);

        holder.viewDataBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mItems !=null ? mItems.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        //This can be used to return different layouts depending on the item type in the position,
        //in this app we only have on item type
        return mItemBinder!=null ? mItemBinder.getLayoutRes():super.getItemViewType(position);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if(mItems !=null)
            mItems.removeOnListChangedCallback(mCustomOnListChangedCallback);
    }

    //TODO create an interface for the following methods
    public void setItems(@Nullable List<T> items){
        //if same reference do nothing
        if(items== mItems)
            return;
        //if mItems is not null, then clear then remove the observable list listener and notify that the list is cleared
        if(mItems != null){
            mItems.removeOnListChangedCallback(mCustomOnListChangedCallback);
            notifyItemRangeRemoved(0, mItems.size());
        }

        if(items instanceof ObservableList){
            //if items is observable, then assign it to mItems, notify that insertion happened
            // and add observable list callback
            mItems = (ObservableList<T>) items;
            notifyItemRangeInserted(0, mItems.size());
            mItems.addOnListChangedCallback(mCustomOnListChangedCallback);
        }else if(items !=null){
            mItems = new ObservableArrayList<>();
            mItems.addOnListChangedCallback(mCustomOnListChangedCallback);
            mItems.addAll(items);
        } else {
            mItems = null;
        }
    }

    @Nullable
    public ObservableList<T> getItems(){
        return mItems;
    }

    @Nullable
    public T getAdapterItem(int position){
        return mItems !=null ? mItems.get(position):null;
    }

    public void setClickHandler(ClickHandler<T> clickHandler){
        mClickHandler = clickHandler;
    }

    public void setLongClickHandler(LongClickHandler<T> longClickHandler){
        mLongClickHandler = longClickHandler;
    }

    public void setItemTouchHandler(ItemTouchHandler<T> itemTouchHandler){
        mItemTouchHandler = itemTouchHandler;
    }

    @Override
    public void onClick(View view) {
        //pass the click notification to handler with the designated item
        if(mClickHandler!=null)
            mClickHandler.onClick((T) view.getTag(ITEM_TAG));
    }

    @Override
    public boolean onLongClick(View view) {
        //pass the long click notification to handler with the designated item
        if(mLongClickHandler!=null) {
            mLongClickHandler.onLongClick((T) view.getTag(ITEM_TAG));
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemSwiped(View view) {
        if(mItemTouchHandler!=null) {
            T item = (T) view.getTag(ITEM_TAG);
            mItemTouchHandler.onItemSwiped(item);
            return true;
        }
        return false;
    }
    //End of methods to be added to the interface

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        //set a reference for the recyclerView in the ListChangedCallback
        mCustomOnListChangedCallback.setRecyclerView(recyclerView);
    }

    /**
     * View Holder Class
     */
    //TODO check if it is necessary that DataBindingViewHolder must implements some interface like ItemTouchHelperViewHolder
    public static class DataBindingViewHolder extends RecyclerView.ViewHolder{
        final ViewDataBinding viewDataBinding;
        public DataBindingViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;
        }
    }

    /**
     * OnListChangedCallback Class
     * @param <T>
     */
    private static class CustomOnListChangedCallback<T> extends ObservableList.OnListChangedCallback{

        //TODO try to declare DataBindingRVAdapter as weak reference
        private final DataBindingRVAdapter<T> dataBindingRVAdapter;
        RecyclerView mRecyclerView;

        public  CustomOnListChangedCallback(DataBindingRVAdapter<T> adapter){
            dataBindingRVAdapter = adapter;
        }

        void setRecyclerView(RecyclerView recyclerView){
            this.mRecyclerView = recyclerView;
        }

        @Override
        public void onChanged(ObservableList sender) {
            RecyclerView.Adapter adapter = dataBindingRVAdapter;
            Log.d("DataBindingRVAdapter","CustomOnListChangedCallback onChanged");
            if(adapter!=null)
                adapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            RecyclerView.Adapter adapter = dataBindingRVAdapter;
            Log.d("DataBindingRVAdapter","CustomOnListChangedCallback onItemRangeChanged");
            if(adapter!=null)
                adapter.notifyItemRangeChanged(positionStart,itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            RecyclerView.Adapter adapter = dataBindingRVAdapter;
            Log.d("DataBindingRVAdapter","CustomOnListChangedCallback onItemRangeInserted");
            if(adapter!=null) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
                if(mRecyclerView!=null){
                    //scroll to top after insertion
                    mRecyclerView.smoothScrollToPosition(positionStart);
                }
            }
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            RecyclerView.Adapter adapter = dataBindingRVAdapter;
            Log.d("DataBindingRVAdapter","CustomOnListChangedCallback onItemRangeMoved");
            if(adapter!=null)
                adapter.notifyItemMoved(fromPosition,toPosition);
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            RecyclerView.Adapter adapter = dataBindingRVAdapter;
            Log.d("DataBindingRVAdapter","CustomOnListChangedCallback onItemRangeRemoved");
            if(adapter!=null)
                adapter.notifyItemRangeRemoved(positionStart,itemCount);
        }
    }
}
