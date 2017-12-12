package com.wearsafe.memo.view;

import android.content.Context;
import android.support.v4.app.LoaderManager;

/**
 * Created by Ali on 10-Dec-17.
 */

public interface AndroidMainView {
    Context getContext();
    LoaderManager getViewLoaderManager();
}
