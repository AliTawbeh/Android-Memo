package com.wearsafe.memo;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.wearsafe.memo.model.MemoContract;



/**
 * Created by Ali on 05-Nov-17.
 * The MemoCursorAdapter populates a recyclerView by creating and binding viewHolders that contain
 * the description and ID of Memo
 */

public class MemoCursorAdapter extends RecyclerView.Adapter<MemoCursorAdapter.MemoViewHolder> {

    //member variables, cursor to hold the data and a context
    private Cursor mCursor;
    private Context mContext;

    /**
     * constructor for the adapter to set the context
     * @param context
     */
    public MemoCursorAdapter(Context context){
        mContext = context;
    }

    /**
     * Creating a view holder and inflating it by the correspondent layout
     * @param parent parent view
     * @param viewType in this app we only have on view type
     * @return inflated MemoViewHolder
     */
    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.memo_layout,parent,false);
        return new MemoViewHolder(view);
    }

    /**
     * Populating each MemoViewHolder with data at each position
     * @param holder The view holder to be filled with data and bound to the recyclerView
     * @param position the position of data in the cursor
     */
    @Override
    public void onBindViewHolder(MemoViewHolder holder, int position) {
        if(mCursor==null || mCursor.getCount()==0)
            return;
        //column ID index
        int indexID = mCursor.getColumnIndex(MemoContract.MemoEntry._ID);
        //column description index
        int indexDesc = mCursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_DESCRIPTION);
        //Moving the cursor to the position
        mCursor.moveToPosition(position);
        /*The id of the memo is needed when the viewHolder is swiped delete, however we do not wish
        to display the id, so we set it as a tag*/
        final int id = mCursor.getInt(indexID);
        holder.itemView.setTag(id);
        //setting the memo description
        String description = mCursor.getString(indexDesc);

        //holder.getBinding().setVariable(BR.memoDescription,description);
        holder.getBinding().executePendingBindings();
    }

    /**
     * Get the size of the adapter data
     * @return the size of the cursor
     */
    @Override
    public int getItemCount() {
        if(mCursor==null)
            return 0;
        return mCursor.getCount();
    }

    /**
     * Update the current cursor
     * @param cursor new cursor
     */
    public void swapCursor(Cursor cursor){
        if(mCursor!=null)
            mCursor.close();
        mCursor = cursor;
        if(mCursor!=null)
            notifyDataSetChanged();
    }

    /**
     * This class represents a memo view holder entry, it basically has a text view to display the
     * Memo description
     */
    class MemoViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        public MemoViewHolder(View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding(){
            return binding;
        }
    }

}
