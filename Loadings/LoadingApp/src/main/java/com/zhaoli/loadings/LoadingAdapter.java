package com.zhaoli.loadings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoli on 2016/6/8.
 */
public class LoadingAdapter extends RecyclerView.Adapter<LoadingAdapter.LoadingViewHolder> {

    private Context context = null;
    private List<View> loadingViewList = new ArrayList<>();

    public LoadingAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<View> data) {
        loadingViewList.addAll(data);
    }

    @Override
    public LoadingAdapter.LoadingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading_item_view, parent, false);
        LoadingViewHolder viewHolder = new LoadingViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LoadingAdapter.LoadingViewHolder holder, int position) {
        holder.rootView.removeAllViews();
        ViewParent viewParent = loadingViewList.get(position).getParent();
        if (viewParent != null) {
            if (viewParent instanceof ViewGroup) {
                ((ViewGroup) viewParent).removeAllViews();
            } else {
                return;
            }
        }
        holder.rootView.addView(loadingViewList.get(position));
    }

    @Override
    public int getItemCount() {
        return loadingViewList.size();
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup rootView = null;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            rootView = (ViewGroup) itemView;
        }
    }
}
