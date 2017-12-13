package com.wearsafe.memo.viewmodel;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.wearsafe.memo.BR;
import com.wearsafe.memo.R;
import com.wearsafe.memo.StringHelper;
import com.wearsafe.memo.dynamicadapter.handlers.ClickHandler;
import com.wearsafe.memo.dynamicadapter.handlers.ItemBinder;
import com.wearsafe.memo.dynamicadapter.handlers.ItemTouchHandler;
import com.wearsafe.memo.dynamicadapter.handlers.LongClickHandler;
import com.wearsafe.memo.model.Memo;
import com.wearsafe.memo.model.MemoContract;
import com.wearsafe.memo.view.AndroidMainView;
import com.wearsafe.memo.view.MessageHelper;

import java.util.Objects;

/**
 * Created by Ali on 18-Nov-17.
 */

public class MemoViewModel extends BaseObservable implements LoaderManager.LoaderCallbacks<Cursor> {

    //Constant for identifying unique loader
    private static final int TASK_LOADER_ID = 0;
    //Constant for logging
    public static final String TAG = MemoViewModel.class.getSimpleName();
    private AndroidMainView mAndroidMainView;
    private MessageHelper mMessageHelper;
    @Bindable
    public final ObservableField<String> memoDescription = new ObservableField<>();
    public TextWatcher watcher(){ return new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!Objects.equals(memoDescription.get(), charSequence.toString())) {
                memoDescription.set(charSequence.toString());
            }
        }
        @Override public void afterTextChanged(Editable s) {}
    };
    }
    @Bindable
    public ObservableArrayList<Memo> mItems;
    public ItemBinder itemBinder = new ItemBinder() {
        @Override
        public int getBindingVariable() {
            return BR.memo;
        }

        @Override
        public int getLayoutRes() {
            return R.layout.memo_layout;
        }
    };

    public MemoViewModel(AndroidMainView androidMainView, MessageHelper messageHelper){
        mAndroidMainView = androidMainView;
        mMessageHelper = messageHelper;
        mItems = new ObservableArrayList<>();
        memoDescription.set("");
    }

    public ClickHandler<Memo> clickHandler = new ClickHandler<Memo>() {
        @Override
        public void onClick(Memo item) {
            mMessageHelper.showMessage(item.getDescription());
        }
    };

    public LongClickHandler<Memo> longClickHandler = new LongClickHandler<Memo>() {
        @Override
        public void onLongClick(Memo item) {
            mMessageHelper.showMessage(item.getDescription());
        }
    };

    /*
     *Adding an item touch helper to the recycler view, so when touch gestures are performed on each view holder,
     *callback methods are triggered to take appropriate actions.
     *We are interested in deleting an item when it is swiped left or right
     */
    public ItemTouchHandler<Memo> itemTouchHandler = new ItemTouchHandler<Memo>() {
        @Override
        public void onItemSwiped(Memo item) {
            int id = item.getMemoId();
            String memoId = Integer.toString(id);
            //Forming the appropriate URI to delete the memo with memoId
            Uri uri = MemoContract.MemoEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(memoId).build();
            mAndroidMainView.getContext().getContentResolver().delete(uri, null, null);
            //Refreshing the loader in order to update the data in the recycler view after delete
            //mAndroidMainView.getViewLoaderManager().restartLoader(TASK_LOADER_ID, null, MemoViewModel.this);
            mItems.remove(item);
        }
    };

    public void onCreate() {
        //Initializing the loader to get the data and populate the recycler view
        mAndroidMainView.getViewLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * Creating an AsyncTaskLoader using its unique ID, TASK_LOADER_ID, that will returns a cursor holding the Memo data.
     * Implementing the callbacks for all phases of loading the data.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(mAndroidMainView.getContext()) {
            //Cursor that will hold the data
            Cursor memoData =null;

            @Override
            protected void onStartLoading() {
                //If data already existing, deliver it immediately, else trigger loading the data
                if(memoData !=null)
                    deliverResult(memoData);
                else
                    forceLoad();
            }
            //querying the memo data asynchronousely
            @Override
            public Cursor loadInBackground() {
                try{
                    return mAndroidMainView.getContext().getContentResolver().query(MemoContract.MemoEntry.CONTENT_URI,
                            null,null,null, MemoContract.MemoEntry._ID + " DESC");
                } catch (Exception e){
                    Log.d(TAG,"Could not load data");
                    e.printStackTrace();
                    return null;
                }
            }
            //delivering the data after loadInBackground is done
            @Override
            public void deliverResult(Cursor data) {
                memoData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * after loading data is done, swapping the recycler's old cursor with the new one
     * @param loader
     * @param data the cursor returned by the loader
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        refreshObservableList(data);
    }

    /**
     * This method is called when a created loader is being reset
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        refreshObservableList(null);
    }

    /**
     * This function is called when the Save memo button is clicked, it saves the typed memo into
     * the database
     * @param view
     */
    public void onClickSaveMemo(View view) {

        //if the input text is empty do nothing
        String input = memoDescription.get();
        if (input.length() == 0) {
            return;
        }

        //if the input text contains HTML tags do nothing
        if(StringHelper.containsHTMLTags(input)){
            clearUI();
            mMessageHelper.showMessage(R.string.invalid_input);
            return;
        }

        //The input is valid, insert the memo in the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.COLUMN_DESCRIPTION, input);
        Uri uri = mAndroidMainView.getContext().getContentResolver().insert(MemoContract.MemoEntry.CONTENT_URI, contentValues);

        //If the Uri is not null, the insertion was successful
        if(uri != null) {
            //Show a toast message to inform the user that the memo is saved
            mMessageHelper.showMessage(R.string.memo_added);
            //Clear the edit text
            clearUI();
            //restart the loader in order to refresh the data in the recyclerView
            //mAndroidMainView.getViewLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
            String memoID = uri.getPathSegments().get(MemoContract.MemoEntry.COLIMN_DESCRIPTION_INDEX);
            mItems.add(0, new Memo(Integer.parseInt(memoID),input));
        }

    }

    //Clear the UI: empty the memo edit text
    private void clearUI(){
        memoDescription.set("");
    }

    private void refreshObservableList(Cursor cursor){
        //mItems = new ObservableArrayList<>();
        mItems.clear();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            //column ID index
            int indexID = cursor.getColumnIndex(MemoContract.MemoEntry._ID);
            //column description index
            int indexDesc = cursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_DESCRIPTION);
            Memo memo = new Memo(cursor.getInt(indexID), cursor.getString(indexDesc));
            mItems.add(memo);
        }
    }
}