package com.wearsafe.memo.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wearsafe.memo.presenter.MemoCursorAdapter;
import com.wearsafe.memo.R;
import com.wearsafe.memo.presenter.MemoPresenter;

public class MainActivity extends AppCompatActivity implements MemoView {

    //presenter member variable
    MemoPresenter mMemoPresenter;
    //editText member variable
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
                //Notify the presenter that an item is swiped, so delete it
                mMemoPresenter.deleteMemo(id);
            }
        });
        //Attaching the item touch helper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        //initializing the presenter
        mMemoPresenter = new MemoPresenter(this);
        mMemoPresenter.onCreate();
    }

    /**
     * This function is called when the Save memo button is clicked, it saves the typed memo into
     * the database
     * @param view
     */
    public void onClickSaveMemo(View view) {
        //if the input text is empty do nothing
        String input = ((EditText) findViewById(R.id.et_memo)).getText().toString();
        mMemoPresenter.saveMemo(input);
    }

    /**
     * Clear the UI: empty the memo edit text, triggered by the presenter
     */
    @Override
    public void clearUI(){
        mMemoEditText.setText("");
        mMemoEditText.requestFocus();
    }

    /**
     * This method is triggered by the presenter, it tells the MemoView to swap the recycler's view
     * cursor
     * @param cursor new cursor
     */
    @Override
    public void updateMemos(Cursor cursor) {
        mMemoCursorAdapter.swapCursor(cursor);
    }

    /**
     * Triggered by the presenter to tell the MemoView to display a toast for the user
     * @param message message to be shown to the user
     */
    @Override
    public void showUIMessage(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Get a reference to the loader manager in order to initiate it from another class
     * @return a reference to support loader manager
     */
    @Override
    public android.support.v4.app.LoaderManager getMemoViewLoaderManager() {
        return  getSupportLoaderManager();
    }

    /**
     * Return activity's context
     * @return a reference to the activity's context
     */
    @Override
    public Context getMemoViewContext() {
        return this;
    }
}
