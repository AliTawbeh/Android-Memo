package com.wearsafe.memo.controler;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wearsafe.memo.R;
import com.wearsafe.memo.model.MemoContract;
import com.wearsafe.memo.utils.StringHelper;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    //Constant for logging
    public static final String TAG = MainActivity.class.getSimpleName();
    //Constant for identifying unique loader
    private static final int TASK_LOADER_ID = 0;

    EditText mMemoEditText;

    //Members for RecyclerView and its Adapter
    RecyclerView mRecyclerView;
    private MemoCursorAdapter mMemoCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing Memo editText
        mMemoEditText = (EditText) findViewById(R.id.et_memo);
        //Initializing recyclerView, setting its layout
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_memos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Initializing the adapter and setting it to the recyclerView
        mMemoCursorAdapter = new MemoCursorAdapter(this);
        mRecyclerView.setAdapter(mMemoCursorAdapter);

        /*
         *Adding an item touch helper to the recycler view, so when touch gestures are performed on each view holder,
         *callback methods are triggered to take appropriate actions.
         *We are interested in deleting an item when it is swiped left or right
         */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            //This callback is triggered when a user swipes left or right an item
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //getting the Memo ID using the viewHolder's tag
                int id = (int) viewHolder.itemView.getTag();
                String memoId = Integer.toString(id);
                //Forming the appropriate URI to delete the memo with memoId
                Uri uri = MemoContract.MemoEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(memoId).build();
                getContentResolver().delete(uri, null, null);
                //Refreshing the loader in order to update the data in the recycler view after delete
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);

            }
        });
        //Attaching the item touch helper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        //Initializing the loader to get the data and populate the recycler view
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
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
        return new AsyncTaskLoader<Cursor>(this) {
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
                    return getContentResolver().query(MemoContract.MemoEntry.CONTENT_URI,
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
     * after loading data is done, swapping the recycler's old cursor with the new one
     * @param loader
     * @param data the cursor returned by the loader
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMemoCursorAdapter.swapCursor(data);
    }

    /**
     * This method is called when a created loader is being reset
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMemoCursorAdapter.swapCursor(null);
    }

    /**
     * This function is called when the Save memo button is clicked, it saves the typed memo into
     * the database
     * @param view
     */
    public void onClickSaveMemo(View view) {
        //if the input text is empty do nothing
        String input = ((EditText) findViewById(R.id.et_memo)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        //if the input text contains HTML tags do nothing
        if(StringHelper.containsHTMLTags(input)){
            clearUI();
            Toast.makeText(getBaseContext(), getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
            return;
        }

        //The input is valid, insert the memo in the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.COLUMN_DESCRIPTION, input);
        Uri uri = getContentResolver().insert(MemoContract.MemoEntry.CONTENT_URI, contentValues);

        //If the Uri is not null, the insertion was successful
        if(uri != null) {
            //Show a toast message to inform the user that the memo is saved
            Toast.makeText(getBaseContext(), getString(R.string.memo_added), Toast.LENGTH_LONG).show();
            //Clear the edit text
            clearUI();
            //restart the loader in order to refresh the data in the recyclerView
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }
    }

    //Clear the UI: empty the memo edit text
    private void clearUI(){
        mMemoEditText.setText("");
        mMemoEditText.requestFocus();
    }
}
