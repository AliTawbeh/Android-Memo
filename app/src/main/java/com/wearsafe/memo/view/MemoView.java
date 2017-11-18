package com.wearsafe.memo.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Ali on 17-Nov-17.
 * This interface is used to eliminate coupling the presenter to a specific view,
 * so the activity implements this interface and the presenter code in it
 */

public interface MemoView {
    void updateMemos(Cursor cursor);
    void clearUI();
    void showUIMessage(String message);
    LoaderManager getMemoViewLoaderManager();
    Context getMemoViewContext();

}
