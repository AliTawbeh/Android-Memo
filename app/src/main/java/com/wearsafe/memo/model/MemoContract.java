package com.wearsafe.memo.model;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ali on 05-Nov-17.
 * This Contract class contains the content provider constants
 * Clients use this class to know how to access the Memo data.
 */

public class MemoContract {

    //The AUTHORITY tells which content provider to access
    public static final String AUTHORITY = "com.wearsafe.memo";

    //The base content Uri to access Memo data
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //The path to access the memo data
    public static final String PATH_MEMOS = "memos";

    //This inner class defines the Memo table
    public static final class MemoEntry implements BaseColumns {
        //The content URI to access the Memo table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEMOS).build();

        //Memo table
        public static final String TABLE_NAME = PATH_MEMOS;

        //description column
        public static final String COLUMN_DESCRIPTION = "description";
    }
}
