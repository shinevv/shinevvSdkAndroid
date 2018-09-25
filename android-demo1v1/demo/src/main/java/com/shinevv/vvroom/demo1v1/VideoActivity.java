package com.shinevv.vvroom.demo1v1;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.shinevv.vvroom.IVVListener;
import com.shinevv.vvroom.Shinevv;
import com.shinevv.vvroom.demo1v1.view.PeerView;
import com.shinevv.vvroom.modles.VVChatMsg;
import com.shinevv.vvroom.modles.VVPeers;
import com.shinevv.vvroom.modles.VVUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.VideoTrack;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;

public class VideoActivity extends AppCompatActivity implements IVVListener.IVVMediaListener ,IVVListener.IVVChatListener,IVVListener.IVVConnectionListener,IVVListener.IVVClassListener{


    @BindView(R.id.myselfVideo)
    PeerView myselfVideo;
    @BindView(R.id.otherVideo)
    PeerView otherVideo;
    @BindView(R.id.hangup)
    ImageView gua;
    private PeerView peerView;
    private Shinevv shinevvClient;
    private String userName, peerId,remotePeerId,chatRoomId,roomId;
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        keepScreenLongLight(this);
        otherVideo.bringToFront();
        peerView = new PeerView(VideoActivity.this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BaseApplication app = (BaseApplication) getApplication();
        mSocket = app.getSocket();
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        String type = intent.getStringExtra("type");
        roomId = intent.getStringExtra("roomId");
        peerId = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
        checkAudioCameraPermission();

        shinevvClient = new Shinevv(getApplicationContext()
                , roomId           // 房间号
                , (type.equals("audio")) ? Shinevv.TRACK_KINE_AUDIO : Shinevv.TRACK_KINE_VIDEO
                , peerId
                , userName
                , "vvroom.shinevv.cn", 3443, "06175684da8706a0da7e0a6fb2aa8d02"
        );

        //Toast.makeText(this, (type.equals("audio")) ? "语音通话" : "视频通话", Toast.LENGTH_SHORT).show();

        shinevvClient.addShinevvListener(this);
        shinevvClient.joinRoom();
        shinevvClient.setSpeakerEnable(false);
        myselfVideo.init(shinevvClient.getEglBaseContext());
        otherVideo.init(shinevvClient.getEglBaseContext());
        //視頻
        if (type.equals("video")){
            myselfVideo.setMirror(true);
        }else if (type.equals("audio")){ //音頻
            peerView.onVideoClose();
            peerView.onAudioResume();
        }

    }

    /**
     * 是否使屏幕常亮
     *
     * @param activity
     */
    public static void keepScreenLongLight(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        if (shinevvClient != null) {
            myselfVideo.dispose();
            otherVideo.dispose();

            shinevvClient.dispose();
            shinevvClient.removeShinevvListener(this);
            shinevvClient = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.otherVideo, R.id.hangup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.otherVideo:
                break;
            case R.id.hangup:
                Intent intent = new Intent(VideoActivity.this, MainActivity.class);
                setResult(0,intent);
                mSocket.emit("call end", Integer.parseInt(roomId));
                finish();
                clearShinevv();
                break;
        }
    }

    @Override
    public void onRecTextMessage(@NonNull VVChatMsg vvChatMsg) {

    }


    //创建会话失败
    @Override
    public void onCreateSessionFail(String s) {
        Toast.makeText(this, "create session fail", Toast.LENGTH_LONG).show();
//        finish();
    }

    // 本端视频回调
    @Override
    public void onAddLocalVideoTrack(VideoTrack videoTrack) {
        myselfVideo.setMediaTrackInfo(videoTrack);
        myselfVideo.setMirror(true);
        otherVideo.setOnTop(true);
    }

    protected void clearShinevv() {
        if (shinevvClient != null) {
            shinevvClient.dispose();
            shinevvClient.removeShinevvListener(this);
            shinevvClient = null;
        }
    }

    // 远端视频回调
    @Override
    public void onAddRemoteVideoTrack(VideoTrack videoTrack, VVUser vvUser) {
        otherVideo.setMediaTrackInfo(videoTrack);
//        otherVideo.bringToFront();
        otherVideo.setOnTop(true);

        // 为了节省带宽，远端视频默认不接受，需要手动开启
        shinevvClient.pauseRemotePeerVideo(vvUser.getPeerId(), false);
    }

    // 收到关闭/开启视频消息
    @Override
    public void onReceiveVideoSilent(boolean status) {
        if (status) {
            // waiting for changing state by onAddLocalVideoTrack
        } else {
            myselfVideo.onVideoClose();
        }
    }

    // 收到关闭/开启音频消息
    @Override
    public void onReceiveAudioSilent(boolean status) {
        if (status) {
            myselfVideo.onAudioResume();
        } else {
            myselfVideo.onAudioPaused();
        }
    }

    // 成员视频关闭
    @Override
    public void onRemoteVideoClose(String s) {
        otherVideo.onVideoClose();
        myselfVideo.onVideoClose();
    }

    // 成员音频暂停
    @Override
    public void onRemoteAudioPaused(String s) {
        otherVideo.onAudioPaused();
    }

    // 成员音频开启
    @Override
    public void onRemoteAudioResume(String s) {
        otherVideo.onAudioResume();
    }

    @Override
    public void onClassStart(VVPeers vvPeers) {
        for(VVUser vvUser : vvPeers.getPeers()){
            onNewRemotePeer(vvUser);
        }
    }

    @Override
    public void onClassOver() {
        myselfVideo.onPeerClosed();

    }

    @Override
    public void onNewRemotePeer(VVUser vvUser) {

    }

    @Override
    public void onRemotePeerClose(String s) {
        peerView.onPeerClosed();
        Intent intent = new Intent(VideoActivity.this, MainActivity.class);
        setResult(0,intent);
        finish();
        clearShinevv();
    }


    // request permission
    private static final int ASK_DEVICE_PERMISSION = 10;
    private boolean noAudioPermission;
    private boolean noVideoPermission;
    private boolean noExternalStoragePermission;

    private void checkAudioCameraPermission() {
        noAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED;
        noVideoPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED;
        noExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;

        // Here, thisActivity is the current activity
        if (noAudioPermission || noVideoPermission || noExternalStoragePermission) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                //Toast.makeText(this, R.string.please_grant_device, Toast.LENGTH_LONG).show();
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //Toast.makeText(this, R.string.please_grant_device, Toast.LENGTH_LONG).show();
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(this, R.string.please_grant_device, Toast.LENGTH_LONG).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ASK_DEVICE_PERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != ASK_DEVICE_PERMISSION || permissions.length == 0 || grantResults.length == 0)
            return;

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.RECORD_AUDIO) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                noAudioPermission = false;
            } else if (permissions[i].equals(Manifest.permission.CAMERA) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                noVideoPermission = false;
            } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                noExternalStoragePermission = false;
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (shinevvClient != null) {
            shinevvClient.dispose();
            shinevvClient.removeShinevvListener(this);
            shinevvClient = null;
        }
        super.onBackPressed();
    }


    @Override
    public void onConnected() {
        Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectFail() {
        Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();
    }


}