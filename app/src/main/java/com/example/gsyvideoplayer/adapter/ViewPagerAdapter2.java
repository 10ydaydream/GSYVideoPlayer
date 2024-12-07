package com.example.gsyvideoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gsyvideoplayer.R;
import com.example.gsyvideoplayer.holder.RecyclerItemNormalHolder;
import com.example.gsyvideoplayer.model.VideoModel;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class ViewPagerAdapter2 extends BannerAdapter<VideoModel, RecyclerView.ViewHolder> {
    private final static String TAG = "RecyclerBaseAdapter";

    private List<VideoModel> itemDataList = null;
    private Context context = null;

    public ViewPagerAdapter2(Context context, List<VideoModel> itemDataList) {
        super(itemDataList);
        this.itemDataList = itemDataList;
        this.context = context;
    }

    public void setListData(List<VideoModel> data) {
        itemDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_viewpager2_item, parent, false);
        final RecyclerView.ViewHolder holder = new RecyclerItemNormalHolder(context, v);
        return holder;
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, VideoModel data, int position, int size) {
        RecyclerItemNormalHolder recyclerItemViewHolder = (RecyclerItemNormalHolder) holder;
        recyclerItemViewHolder.setRecyclerBaseAdapter(this);
        recyclerItemViewHolder.onBind(position, itemDataList.get(position));
    }
}
