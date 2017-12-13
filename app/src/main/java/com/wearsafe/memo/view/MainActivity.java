package com.wearsafe.memo.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;


import com.wearsafe.memo.R;
import com.wearsafe.memo.databinding.ActivityMainBinding;
import com.wearsafe.memo.model.MemoContract;
import com.wearsafe.memo.viewmodel.MemoViewModel;

public class MainActivity extends AppCompatActivity implements AndroidMainView, MessageHelper {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        MemoViewModel memoViewModel = new MemoViewModel(this,this);
        binding.setViewModel(memoViewModel);
        memoViewModel.onCreate();
        binding.rvMemos.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int messageId) {
        showMessage(getString(messageId));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LoaderManager getViewLoaderManager() {
        return this.getSupportLoaderManager();
    }
}
