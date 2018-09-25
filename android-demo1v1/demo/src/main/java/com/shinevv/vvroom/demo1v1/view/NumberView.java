package com.shinevv.vvroom.demo1v1.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinevv.vvroom.demo1v1.R;
import com.shinevv.vvroom.modles.VVTransportInfo;
import com.shinevv.vvroom.modles.VVUser;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

public class NumberView extends FrameLayout {

    public NumberView(@NonNull Context context) {
        super(context);
        init();
    }

    public NumberView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NumberView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private boolean inited = false;
    private TextView tvName;
    private VVUser peer;
    private EglBase.Context glEglBaseContext;
    private boolean surfaceViewInited = false;
    private boolean mirror;
    private ImageView mPhone;

    private void init(){
        if(inited) return;
        inited = true;
        View view = inflate(getContext(), R.layout.number_view, this);
        tvName = view.findViewById(R.id.number_name);
        mPhone = view.findViewById(R.id.mPhone);
//        mPhone.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
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
    }

    public boolean hasPeerInfo(){
        return this.peer!=null;
    }




    /**
     * 镜像显示
     * @param mirror mirror
     */
    public void setMirror(boolean mirror){
        this.mirror = mirror;
        if(surfaceViewInited){

        }else{
            // wait for init
        }
    }


}
