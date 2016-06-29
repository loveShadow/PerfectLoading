package com.zhaoli.loadings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhaoli.loadings.loadingViews.ConvertBallLoading;
import com.zhaoli.loadings.loadingViews.DownloadLoading;
import com.zhaoli.loadings.loadingViews.JumpWhirlGraphLoading;
import com.zhaoli.loadings.loadingViews.LineWhirlLoading1;
import com.zhaoli.loadings.loadingViews.LineWhirlLoading2;
import com.zhaoli.loadings.loadingViews.TranslationBallLoading;
import com.zhaoli.loadings.loadingViews.RotateSquareLoading;
import com.zhaoli.loadings.loadingViews.SegmentSquareLoading;
import com.zhaoli.loadings.loadingViews.SwingCollisionLoading;

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

        loadingViewList.add(new DownloadLoading(this));
        loadingViewList.add(new LineWhirlLoading1(this));
        loadingViewList.add(new LineWhirlLoading2(this));
        loadingViewList.add(new ConvertBallLoading(this));
        loadingViewList.add(new JumpWhirlGraphLoading(this));
        loadingViewList.add(new TranslationBallLoading(this));
        loadingViewList.add(new SegmentSquareLoading(this));
        loadingViewList.add(new SwingCollisionLoading(this));
        loadingViewList.add(new RotateSquareLoading(this));

        adapter.setData(loadingViewList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
