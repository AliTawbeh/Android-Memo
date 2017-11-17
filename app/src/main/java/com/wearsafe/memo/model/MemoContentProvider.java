package com.wearsafe.memo.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ali on 05-Nov-17.
 */

public class MemoContentProvider extends ContentProvider {

    //Integer codes for memo directories and individual items
    public static final int MEMO_CODE = 100;
    public static final int MEMO_ITEM_CODE = 101;

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    private MemoDbHelper mMemoDbHelper;

    /**
     * Associate each Uri format type a code
     * @return a Uri Matcher
     */
    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Matching against the Uri of the whole Memos directory
        uriMatcher.addURI(MemoContract.AUTHORITY,MemoContract.PATH_MEMOS,MEMO_CODE);
        //Matching against the Uri of an individual Memo given its ID in the Uri
        uriMatcher.addURI(MemoContract.AUTHORITY,MemoContract.PATH_MEMOS + "/#",MEMO_ITEM_CODE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        //initializing memoDbHelper
        mMemoDbHelper = new MemoDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //getting reference to the database
        final SQLiteDatabase sqLiteDatabase = mMemoDbHelper.getReadableDatabase();
        //getting the uri type
        int match = mUriMatcher.match(uri);
        Cursor contentCursor;
        switch (match){
            case MEMO_CODE:
                //Querying the Memo table
                contentCursor = sqLiteDatabase.query(MemoContract.MemoEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                //wrong uri
                throw new UnsupportedOperationException("Unsupported operation, wrong URI: " + uri.toString());
        }
        //setting a notification on the URI to notify the client useing the content provider
        // and returning the cursor
        contentCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return contentCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //getting reference to the database
        SQLiteDatabase sqLiteDatabase = mMemoDbHelper.getWritableDatabase();
        //getting the uri type
        int match = mUriMatcher.match(uri);
        Uri returnedUri;
        switch (match){
            case MEMO_CODE:
                //inserting data into the database
                long id = sqLiteDatabase.insert(MemoContract.MemoEntry.TABLE_NAME,null,contentValues);
                if(id>0)
                    returnedUri = ContentUris.withAppendedId(MemoContract.MemoEntry.CONTENT_URI, id);
                else
                    throw new android.database.SQLException("Could not insert data in " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation, wrong URI: " + uri.toString());
        }
        //Notifying the resolver that the database data has changed
        getContext().getContentResolver().notifyChange(uri,null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //getting reference to the database
        SQLiteDatabase sqLiteDatabase = mMemoDbHelper.getWritableDatabase();
        //getting the uri type
        int match = mUriMatcher.match(uri);
        int rowsDeleted;
        switch (match){
            case MEMO_ITEM_CODE:
                //Getting the id of the memo to delete
                String memoID = uri.getPathSegments().get(1);
                rowsDeleted = sqLiteDatabase.delete(MemoContract.MemoEntry.TABLE_NAME,"_id=?",new String[]{memoID});
                if(rowsDeleted>0)
                    getContext().getContentResolver().notifyChange(uri,null);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operation, wrong URI: " + uri.toString());
        }
        //returning the number of deleted rows
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
