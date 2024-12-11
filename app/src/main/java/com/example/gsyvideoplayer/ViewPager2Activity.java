package com.example.gsyvideoplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gsyvideoplayer.adapter.ViewPagerAdapter;
import com.example.gsyvideoplayer.adapter.ViewPagerAdapter2;
import com.example.gsyvideoplayer.databinding.ActivityViewPager2Binding;
import com.example.gsyvideoplayer.holder.RecyclerItemNormalHolder;
import com.example.gsyvideoplayer.model.VideoModel;
import com.example.gsyvideoplayer.video.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Activity extends AppCompatActivity {
    public static final String TAG = "ViewPager2Activity";
    ActivityViewPager2Binding binding;

    List<VideoModel> dataList = new ArrayList<>();

    //    ViewPagerAdapter viewPagerAdapter;
    ViewPagerAdapter2 viewPagerAdapter;


    public static long currentPlayPosition = -1;
    public static boolean needAutoPlay = true;
    private SampleCoverVideo mSampleCoverVideo;
    private boolean hasClone = false;
    public static Bitmap mShot = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentPlayPosition = -1;
        binding = ActivityViewPager2Binding.inflate(getLayoutInflater());

        View rootView = binding.getRoot();
        setContentView(rootView);

        mSampleCoverVideo = new SampleCoverVideo(this);
        String url = "https://pointshow.oss-cn-hangzhou.aliyuncs.com/McTk51586843620689.mp4";
        String title = "这是title";
        GSYVideoOptionBuilder gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        gsyVideoOptionBuilder
            .setIsTouchWiget(false)
            //.setThumbImageView(imageView)
            .setUrl(url)
//            .setVideoTitle(title)
            .setCacheWithPlay(true)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setPlayTag(RecyclerItemNormalHolder.TAG)
//            .setMapHeadData(header)
            .setShowFullAnimation(true)
            .setNeedLockFull(true)
            .setPlayPosition(0)
            .setThumbPlay(true)
            .setVideoAllCallBack(new GSYSampleCallBack() {
            }).build(mSampleCoverVideo);

        resolveData();
        viewPagerAdapter = new ViewPagerAdapter2(this, dataList);
        binding.banner.setAdapter(viewPagerAdapter);
        Log.i(TAG, "onCreate: >>> offset limit = " + binding.banner.getViewPager2().getOffscreenPageLimit());
        binding.banner.addOnPageChangeListener(new OnPageChangeListener() {
            int dragPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: >>> position = " + position);
                int vpPosition = binding.banner.getViewPager2().getCurrentItem();
                Log.i(TAG, "onPageSelected: >>> vp position = " + vpPosition);
                int rawPosition = viewPagerAdapter.getRealPosition(position);
                Log.i(TAG, "onPageScrollStateChanged: >>> raw position = " + rawPosition);
                if (vpPosition /*% 2*/ == 1 || vpPosition == 7) {
                    Log.i(TAG, "onPageSelected: >>> double");
                } else {
                    Log.i(TAG, "onPageSelected: >>> single");
                    return;
                }
                int playPosition = GSYVideoManager.instance().getPlayPosition();
                Log.i(TAG, "onPageSelected: >>> playPosition = " + playPosition);
                if (hasClone) {
                    playPosition = mSampleCoverVideo.getPlayPosition();
                    Log.i(TAG, "onPageSelected: >>> clone playPosition = " + playPosition);
                }
//                if (playPosition >= 0) {
//                    //对应的播放列表TAG
//                    Log.i(TAG, "onPageSelected: >>> tag = " + GSYVideoManager.instance().getPlayTag());
//                    Log.i(TAG, "onPageSelected: >>> RecyclerItemNormalHolder.TAG = " + RecyclerItemNormalHolder.TAG);
//                    if (GSYVideoManager.instance().getPlayTag().equals(RecyclerItemNormalHolder.TAG)) {
//                        playPosition(vpPosition);
//                    } else if (hasClone && mSampleCoverVideo.getPlayTag().equals(RecyclerItemNormalHolder.TAG)) {
//                        Log.i(TAG, "onPageSelected: >>> clone player start.");
//                        playPosition(vpPosition);
//                    }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int nowPosition = binding.banner.getCurrentItem();
                int rawPosition = viewPagerAdapter.getRealPosition(nowPosition);
                Log.i(TAG, "onPageScrollStateChanged: >>> state = " + state);
                Log.i(TAG, "onPageScrollStateChanged: >>> current position = " + nowPosition);
                Log.i(TAG, "onPageScrollStateChanged: >>> raw position = " + rawPosition);
                Log.i(TAG, "onPageScrollStateChanged: >>> dragPosition = " + dragPosition);
                Log.i(TAG, "onPageScrollStateChanged: >>> vp position = " + binding.banner.getViewPager2().getCurrentItem());
                // 变化SCROLL_STATE_DRAGGING->SCROLL_STATE_SETTLING->SCROLL_STATE_IDLE
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    dragPosition = nowPosition;
                    if (dragPosition == 1) {
                        stopPosition(nowPosition);
//                        updateCurrentPlayPosition(nowPosition);
                    }
                } else if (state == ViewPager2.SCROLL_STATE_SETTLING) {

                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (nowPosition == 1 && (dragPosition == 1 || dragPosition == 2)) {
                        playPosition(dragPosition);
                    } else if (dragPosition > 0 && Math.abs(nowPosition - dragPosition) > 1) {
                        if (dragPosition > nowPosition) {
                            // 尾部回到头部
                            playPosition(nowPosition);
                        }
                    }
                    dragPosition = -1;
                }
            }
        });
        binding.banner.post(() -> {
            playPosition(0);
        });
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    private void resolveData() {
        for (int i = 0; i < 6; i++) {
            VideoModel videoModel = new VideoModel();
            dataList.add(videoModel);
        }
        if (viewPagerAdapter != null)
            viewPagerAdapter.notifyDataSetChanged();
    }


    private void playPosition(int position) {
        Log.i(TAG, "playPosition: >>> needAutoPlay = " + needAutoPlay);
//        if (!needAutoPlay) {
//            Log.i(TAG, "playPosition: not need auto play.");
//            return;
//        }
        mShot = null;
        binding.banner.postDelayed(() -> {
            Log.i(TAG, "playPosition: >>> in post.");
            RecyclerView.ViewHolder viewHolder = ((RecyclerView)
                binding.banner.getViewPager2().getChildAt(0))
                .findViewHolderForAdapterPosition(binding.banner.getViewPager2().getCurrentItem());
            Log.i(TAG, "playPosition: >>> viewHolder = " + viewHolder);
            if (viewHolder != null) {
                RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) viewHolder;
                Log.i(TAG, "playPosition: >>> currentPlayPosition = " + currentPlayPosition);
                int currentState = recyclerItemNormalHolder.getPlayer().getCurrentState();
                Log.i(TAG, "playPosition: >>> current play state = " + currentState);
                Log.i(TAG, "playPosition: >>> hasClone = " + hasClone);
                if (currentState == GSYVideoView.CURRENT_STATE_PAUSE) {
                    Log.i(TAG, "playPosition: >>> resume");
                    if (needAutoPlay) {
                        Log.i(TAG, "playPosition: >>> resume, not need");
                        recyclerItemNormalHolder.getPlayer().onVideoResume();
                    }
                } else {
                    Log.i(TAG, "playPosition: >>> start");
                    if (hasClone) {
                        recyclerItemNormalHolder.getPlayer().cloneState(mSampleCoverVideo);
                    }
                    if (currentPlayPosition != -1) {
                        recyclerItemNormalHolder.getPlayer().setSeekOnStart(currentPlayPosition);
                    }
                    if (needAutoPlay) {
                        Log.i(TAG, "playPosition: >>> start, auto start.");
                        recyclerItemNormalHolder.getPlayer().startPlayLogic();
                    } else {
                        Log.i(TAG, "playPosition: >>> start, not need");
                        recyclerItemNormalHolder.getPlayer().startPlayLogic();
                        Log.i(TAG, "playPosition: >>> pause");
                        if (mShot != null) {
                            recyclerItemNormalHolder.getPlayer().resetToCoverImage(mShot);
                        }
                        recyclerItemNormalHolder.getPlayer().onVideoPause();
                    }
                }
                needAutoPlay = true;
                hasClone = false;
            }
        }, 32);
    }

    private void stopPosition(int position) {
        binding.banner.postDelayed(() -> {
            Log.i(TAG, "stopPosition: >>> in post.");
            RecyclerView.ViewHolder viewHolder = ((RecyclerView)
                binding.banner.getViewPager2().getChildAt(0))
                .findViewHolderForAdapterPosition(binding.banner.getViewPager2().getCurrentItem());
            Log.i(TAG, "stopPosition: >>> viewHolder = " + viewHolder);
            if (viewHolder != null) {
                RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) viewHolder;
                currentPlayPosition = recyclerItemNormalHolder.getPlayer().getCurrentPositionWhenPlaying();
                recyclerItemNormalHolder.getPlayer().onVideoPause();
                mShot = Bitmap.createBitmap(recyclerItemNormalHolder.getPlayer().getCurrentFrameBitmap());
                mSampleCoverVideo.cloneState(recyclerItemNormalHolder.getPlayer());
                Log.i(TAG, "stopPosition: >>> mSampleCoverVideo = " + mSampleCoverVideo);
                hasClone = true;
            }
        }, 0);
    }

    private void updateCurrentPlayPosition(int position) {
//        RecyclerView.ViewHolder viewHolder = ((RecyclerView)
//            binding.banner.getViewPager2().getChildAt(0))
//            .findViewHolderForAdapterPosition(binding.banner.getViewPager2().getCurrentItem());
//        Log.i(TAG, "getCurrentPlayPosition: >>> viewHolder = " + viewHolder);
//        if (viewHolder != null) {
//            RecyclerItemNormalHolder recyclerItemNormalHolder = (RecyclerItemNormalHolder) viewHolder;
//            currentPlayPosition = recyclerItemNormalHolder.getPlayer().getCurrentPositionWhenPlaying();
//            Log.i(TAG, "updateCurrentPlayPosition: >>> currentPlayPosition = " + currentPlayPosition);
//            mSampleCoverVideo.cloneState(recyclerItemNormalHolder.getPlayer());
//            hasClone = true;
//        }
    }
}



