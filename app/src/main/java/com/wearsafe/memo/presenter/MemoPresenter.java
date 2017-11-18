package com.wearsafe.memo.presenter;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.wearsafe.memo.R;
import com.wearsafe.memo.model.MemoContract;
import com.wearsafe.memo.utils.StringHelper;
import com.wearsafe.memo.view.MemoView;

/**
 * Created by Ali on 17-Nov-17.
 * This class plays the rol of the presenter in an MVP design. It gets notified by the view (Activity)
 * when an event occurs (adding a memo, deleting a memo), it takes the appropriate action by communicating
 * with the model to insert or delete a memo
 */

public class MemoPresenter implements Presenter, LoaderManager.LoaderCallbacks<Cursor>{

    //Task loader ID
    private static final int TASK_LOADER_ID = 0;
    //Tag for logging
    public static final String TAG = MemoPresenter.class.getSimpleName();
    //Member variable designating the view that will receive events from the user and receive events (activity
    MemoView mMemoView;

    /**
     * constructor initializing the class
     * @param memoView used to initialize the member variable
     */
    public MemoPresenter(MemoView memoView){
        mMemoView = memoView;
    }

    /**
     * Restart the asyncTask data loader
     */
    private void reloadData(){
        //Refreshing the loader in order to update the data in the recycler view after delete
        mMemoView.getMemoViewLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * Tells the model to delete a memo with the given id
     * @param memoId memo id to delete
     */
    public void deleteMemo(int memoId){
        String memoIdStr = Integer.toString(memoId);
        //Forming the appropriate URI to delete the memo with memoId
        Uri uri = MemoContract.MemoEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(memoIdStr).build();
        mMemoView.getMemoViewContext().getContentResolver().delete(uri, null, null);
        //reload data after deleting
        reloadData();
    }

    /**
     * Tells the model to save a memo to the database
     * @param input string memo to be inserted in the data base
     */
    public void saveMemo(String input){
        StringHelper.INPUT_VALIDITY inputValidity = StringHelper.validateInput(input);
        switch (inputValidity){
            case CONTAINS_HTML:
                //if the input text contains HTML tags, show a message and nothing else
                mMemoView.clearUI();
                mMemoView.showUIMessage(mMemoView.getMemoViewContext().getString(R.string.invalid_input));
                break;
            case VALID:
                //The input is valid, insert the memo in the database
                saveMemoToDataBase(input);
            default:
                return;
        }
    }

    /**
     * Helper method to save the input into the database
     * @param input
     */
    private void saveMemoToDataBase(String input) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.COLUMN_DESCRIPTION, input);
        Uri uri = mMemoView.getMemoViewContext().getContentResolver().insert(MemoContract.MemoEntry.CONTENT_URI, contentValues);

        //If the Uri is not null, the insertion was successful
        if(uri != null) {
            //Show a toast message to inform the user that the memo is saved
            mMemoView.showUIMessage(mMemoView.getMemoViewContext().getString(R.string.memo_added));
            //Clear the edit text
            mMemoView.clearUI();
            //restart the loader in order to refresh the data in the recyclerView
            reloadData();
        }
    }

    /**
     * Called when the presenter is created in order to initiate the loader
     */
    @Override
    public void onCreate() {
        //Initializing the loader to get the data and populate the recycler view
        mMemoView.getMemoViewLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

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
        return new AsyncTaskLoader<Cursor>(mMemoView.getMemoViewContext()) {
            //Cursor that will hold the data
            Cursor mMemoData=null;

            @Override
            protected void onStartLoading() {
                //If data already existing, deliver it immediately, else trigger loading the data
                if(mMemoData!=null)
                    deliverResult(mMemoData);
                else
                    forceLoad();
            }
            //querying the memo data asynchronousely
            @Override
            public Cursor loadInBackground() {
                try{
                    return mMemoView.getMemoViewContext().getContentResolver().query(MemoContract.MemoEntry.CONTENT_URI,
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
                mMemoData = data;
                super.deliverResult(data);
            }
        };
    }


    /**
     * This function is called when the AsyncTask loader finishes loading the data
     * @param loader
     * @param data the cursor returned by the loader
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //after loading data is done, swapping the recycler's old cursor with the new one
        mMemoView.updateMemos(data);
    }

    /**
     * This method is called when a created loader is being reset
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMemoView.updateMemos(null);
    }
}
