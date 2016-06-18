package com.zhaoli.loadings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhaoli.loadings.loadingViews.SwingCollisionLoading;
import com.zhaoli.loadings.loadingViews.TestView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2016/6/8.
 */
public class MainActivity extends Activity {

    private RecyclerView recyclerView = null;
    private LoadingAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        recyclerView = (RecyclerView) findViewById(R.id.loadingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LoadingAdapter(this);

        List<View> loadingViewList = new ArrayList<>();

        loadingViewList.add(new SwingCollisionLoading(this));

        adapter.setData(loadingViewList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
