package com.shinevv.vvroom.demo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinevv.vvroom.demo.R;
import com.shinevv.vvroom.modles.VVTransportInfo;
import com.shinevv.vvroom.modles.VVUser;

import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

/**
 * Created by wuhy@shinevv.com on 2018/1/31.
 */
public class PeerView extends FrameLayout {

    private static final String TAG = PeerView.class.getSimpleName();

    public PeerView(@NonNull Context context) {
        super(context);
        init();
    }

    public PeerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PeerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PeerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private boolean inited = false;
    private SurfaceViewRenderer svrPeer;
    private View vEmpty;
    private TextView tvName;
    private ImageView ivNetworkLevel;
    private VVUser peer;

    private boolean surfaceViewInited = false;
    private EglBase.Context glEglBaseContext;
    private boolean mirror;
    private boolean onTop;

    private VideoTrack videoTrack;
    private VideoRenderer videoRenderer;

    private void init(){
        if(inited) return;
        inited = true;

        View view = inflate(getContext(), R.layout.view_peer, this);

        svrPeer = view.findViewById(R.id.peer_video_view);
        vEmpty = view.findViewById(R.id.peer_empty_view);
        tvName = view.findViewById(R.id.peer_name);
        ivNetworkLevel = view.findViewById(R.id.peer_network_level);
    }

    /**
     * 初始化
     * @param glEglBaseContext glEglBaseContext
     */
    public void init(EglBase.Context glEglBaseContext){
        this.glEglBaseContext = glEglBaseContext;
    }

    /**
     * 设置用户信息
     * @param peer 成员信息
     */
    public void setPeerInfo(VVUser peer){
        if(peer == null) return;

        this.peer = peer;
        this.tvName.setText(this.peer.getDisplayName());
        this.tvName.setVisibility(View.VISIBLE);
        this.ivNetworkLevel.setVisibility(View.VISIBLE);
    }

    public boolean hasPeerInfo(){
        return this.peer!=null;
    }

    /**
     * 显示网络状况信息
     * @param transportInfo transportInfo
     */
    public void onRecTransportStats(VVTransportInfo transportInfo) {
        showNetworkLevel(ivNetworkLevel, transportInfo.getLevel());
    }

    /**
     * 设置VideoTrack
     * @param videoTrack videoTrack
     */
    public void setMediaTrackInfo(VideoTrack videoTrack){
        Logging.d(TAG, "setMediaTrackInfo");

        if (videoTrack == null) return;

        if(this.videoTrack == videoTrack) return;
        this.videoTrack = videoTrack;

        initSurfaceView();
        this.videoRenderer = new VideoRenderer(svrPeer);
        this.videoTrack.addRenderer(videoRenderer);

        this.vEmpty.setVisibility(View.GONE);
        this.svrPeer.setVisibility(View.VISIBLE);
    }

    /**
     * 成员视频关闭
     */
    public void onVideoClose(){
        Logging.d(TAG, "onVideoClose");

        removeVideoTrack();

        this.vEmpty.setVisibility(View.VISIBLE);
        this.svrPeer.setVisibility(View.GONE);
    }

    /**
     * 成员音频管暂停
     */
    public void onAudioPaused() {
        // TODO:
    }

    /**
     * 成员音频管开启
     */
    public void onAudioResume() {
        // TODO:
    }

    /**
     * 成员退出
     */
    public void onPeerClosed(){
        removeVideoTrack();

        this.peer = null;
        this.tvName.setText("");
        this.tvName.setVisibility(View.GONE);
        this.ivNetworkLevel.setVisibility(View.GONE);

        this.vEmpty.setVisibility(View.VISIBLE);
        this.svrPeer.setVisibility(View.GONE);
    }


    /**
     * 销毁
     */
    public void dispose(){
        removeVideoTrack();
        svrPeer.release();
        surfaceViewInited = false;
    }

    private void removeVideoTrack(){
        if (this.videoTrack == null) return;

        if (this.videoRenderer != null) {
            this.videoTrack.removeRenderer(this.videoRenderer);
            this.videoRenderer = null;
        }
        this.videoTrack = null;
    }

    /**
     * 镜像显示
     * @param mirror mirror
     */
    public void setMirror(boolean mirror){
        this.mirror = mirror;
        if(surfaceViewInited){
            svrPeer.setMirror(mirror);
        }else{
            // wait for init
        }
    }

    private void initSurfaceView(){
        if(surfaceViewInited) return;
        surfaceViewInited = true;

        svrPeer.init(glEglBaseContext, null);
        svrPeer.setZOrderMediaOverlay(true);
        svrPeer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        svrPeer.setMirror(mirror);
        if(this.onTop){
            svrPeer.setZOrderOnTop(this.onTop);
        }
    }

    public void setOnTop(boolean onTop) {
        this.onTop = onTop;
        if(surfaceViewInited){
            svrPeer.setZOrderOnTop(this.onTop);
        }else{
            // wait for init
        }
    }

    private void showNetworkLevel(ImageView imageView, int level){
        if(level == VVTransportInfo.LEVEL_HIGH){
            imageView.setImageResource(R.mipmap.ic_net_level_high);
        }else if(level == VVTransportInfo.LEVEL_MID){
            imageView.setImageResource(R.mipmap.ic_net_level_mid);
        }else if(level == VVTransportInfo.LEVEL_WEEK){
            imageView.setImageResource(R.mipmap.ic_net_level_weak);
        }else if(level == VVTransportInfo.LEVEL_LOW){
            imageView.setImageResource(R.mipmap.ic_net_level_low);
        }
    }

}
