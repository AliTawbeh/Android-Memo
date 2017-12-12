package com.wearsafe.memo.model;

/**
 * Created by Ali on 10-Dec-17.
 */

public class Memo {
    private String description;
    private int memoId;

    public Memo(int memoId, String description){
        this.description=description;
        this.memoId=memoId;
    }

    public String getDescription() {
        return description;
    }

    public int getMemoId() {
        return memoId;
    }
}
