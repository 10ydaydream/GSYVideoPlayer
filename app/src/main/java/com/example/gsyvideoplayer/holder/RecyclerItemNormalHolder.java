package com.example.gsyvideoplayer.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.gsyvideoplayer.R;
import com.example.gsyvideoplayer.ViewPager2Activity;
import com.example.gsyvideoplayer.model.VideoModel;
import com.example.gsyvideoplayer.video.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guoshuyu on 2017/1/9.
 */

public class RecyclerItemNormalHolder extends RecyclerItemBaseHolder {

    public final static String TAG = "RecyclerView2List";

    protected Context context;

    SampleCoverVideo gsyVideoPlayer;
    ImageView ivHolder;

    ImageView imageView;

    public GSYVideoOptionBuilder gsyVideoOptionBuilder;

    public RecyclerItemNormalHolder(Context context, View v) {
        super(v);
        this.context = context;
        gsyVideoPlayer = v.findViewById(R.id.video_item_player);
        ivHolder = v.findViewById(R.id.iv_holder);
        imageView = new ImageView(context);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
    }

    public void onBind(final int position, VideoModel videoModel) {
        Log.i(TAG, "onBind: >>> position = " + position + ", " + getBindingAdapterPosition());
        String url;
        String title;
        if (position /*% 2*/ == 0) {
            url = "https://pointshow.oss-cn-hangzhou.aliyuncs.com/McTk51586843620689.mp4";
            title = "这是title";

            gsyVideoPlayer.setVisibility(View.VISIBLE);
            if (ivHolder != null) {
                ivHolder.setVisibility(View.GONE);
            }
        } else {
            url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
            title = "哦？Title？";

            if (ivHolder != null) {
                gsyVideoPlayer.setVisibility(View.GONE);
                ivHolder.setVisibility(View.VISIBLE);
                ivHolder.setImageResource(R.drawable.custom_enlarge);
                return;
            }
        }

        Log.i(TAG, "onBind: >>> 2222 , position = " + position + ", " + getBindingAdapterPosition());
        Map<String, String> header = new HashMap<>();
        header.put("ee", "33");

        //防止错位，离开释放
        //gsyVideoPlayer.initUIState();
        Log.i(TAG, "onBind: >>> position = " + position
            + ", " + getBindingAdapterPosition()
            + ", url = " + url
        );
        gsyVideoOptionBuilder
            .setIsTouchWiget(false)
            //.setThumbImageView(imageView)
            .setUrl(url)
            .setVideoTitle(title)
            .setCacheWithPlay(true)
            .setRotateViewAuto(true)
            .setLockLand(true)
            .setPlayTag(TAG)
            .setMapHeadData(header)
            .setShowFullAnimation(true)
            .setNeedLockFull(true)
            .setPlayPosition(0)
            .setVideoAllCallBack(new GSYSampleCallBack() {

                @Override
                public void onPrepared(String url, Object... objects) {
                    super.onPrepared(url, objects);
                    if (!gsyVideoPlayer.isIfCurrentIsFullscreen()) {
                        //静音
                        GSYVideoManager.instance().setNeedMute(true);
                    }
                }

                @Override
                public void onQuitFullscreen(String url, Object... objects) {
                    super.onQuitFullscreen(url, objects);
                    //全屏不静音
                    GSYVideoManager.instance().setNeedMute(true);
                }

                @Override
                public void onEnterFullscreen(String url, Object... objects) {
                    super.onEnterFullscreen(url, objects);
                    GSYVideoManager.instance().setNeedMute(false);
                    gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                }

                @Override
                public void onClickStartThumb(String url, Object... objects) {
                    super.onClickStartThumb(url, objects);
                    Log.i(TAG, "onClickStartThumb: ");
                    ViewPager2Activity.needAutoPlay = true;
                }

                @Override
                public void onClickStartIcon(String url, Object... objects) {
                    super.onClickStartIcon(url, objects);
                    Log.i(TAG, "onClickStartIcon: >>> ");
                    ViewPager2Activity.needAutoPlay = true;
                }

                @Override
                public void onClickResume(String url, Object... objects) {
                    super.onClickResume(url, objects);
                    Log.i(TAG, "onClickResume: >>> ");
                    ViewPager2Activity.needAutoPlay = true;
                }

                @Override
                public void onClickStop(String url, Object... objects) {
                    super.onClickStop(url, objects);
                    ViewPager2Activity.needAutoPlay = false;
                    long playPosition = gsyVideoPlayer.getCurrentPositionWhenPlaying();
                    Log.i(TAG, "onClickStop: >>> playPosition = " + playPosition);
                    ViewPager2Activity.currentPlayPosition = playPosition;
                }

                @Override
                public void onClickStopFullscreen(String url, Object... objects) {
                    super.onClickStopFullscreen(url, objects);
                    Log.i(TAG, "onClickStopFullscreen: >>> ");
                    ViewPager2Activity.needAutoPlay = false;
                }

                @Override
                public void onClickSeekbar(String url, Object... objects) {
                    super.onClickSeekbar(url, objects);
                    long playPosition = gsyVideoPlayer.getCurrentPositionWhenPlaying();
                    Log.i(TAG, "onClickSeekbar: >>> playPosition = " + playPosition);
                    ViewPager2Activity.currentPlayPosition = playPosition;
                }
            }).build(gsyVideoPlayer);

        //增加title
        gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);

        //设置返回键
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);

        //设置全屏按键功能
        gsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveFullBtn(gsyVideoPlayer);
            }
        });
        gsyVideoPlayer.loadCoverImageBy(R.mipmap.xxx2, R.mipmap.xxx2);
        gsyVideoPlayer.setGSYStateUiListener(new GSYStateUiListener() {
            @Override
            public void onStateChanged(int state) {
                int position = getBindingAdapterPosition();
                Log.i(TAG, "onStateChanged: >>> state = " + state + ", position = " + position);
//                long playPosition = gsyVideoPlayer.getCurrentPositionWhenPlaying();
//                ViewPager2Activity.currentPlayPosition = playPosition;
            }
        });
    }

    /**
     * 全屏幕按键处理
     */
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(context, true, true);
    }

    public SampleCoverVideo getPlayer() {
        return gsyVideoPlayer;
    }
}
