package com.shinevv.vvroom.demo;

import android.Manifest;
import android.content.Intent;
<<<<<<< HEAD
=======
import android.content.pm.ActivityInfo;
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
<<<<<<< HEAD
import android.util.Log;
=======
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shinevv.vvroom.IVVListener;
import com.shinevv.vvroom.Shinevv;
import com.shinevv.vvroom.demo.view.PeerView;
import com.shinevv.vvroom.modles.VVPeers;
import com.shinevv.vvroom.modles.VVTransportInfo;
import com.shinevv.vvroom.modles.VVUser;

<<<<<<< HEAD
=======
import org.webrtc.Logging;
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        IVVListener.IVVConnectionListener, IVVListener.IVVClassListener, IVVListener.IVVMediaListener,
        IVVListener.IVVStatsListener, IVVListener.IVVMembersListener {
<<<<<<< HEAD
    private String TAG = "MainActivity";
=======
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378

    private PeerView peerViewLocal;
    private Map<String, PeerView> peerRemoteViewsMap;
    private List<PeerView> peerViewRemotes;

    private static final String peerId = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
    private String displayName = "android-demo";
    private VVUser currentUser;
    private Shinevv shinevvClient;

    private ImageView ivCameraControl;
    private View vCameraSwitch;
    private ImageView ivPhoneControl;
    private ImageView ivSpeakerControl;
    private boolean enableVideo = true;
    private boolean frontCamera = true;
    private boolean enableAudio = true;
    private boolean enableSpeakerFree = false;

    protected String mediaMode = Shinevv.TRACK_KINE_VIDEO;
    protected String roomId;
    protected String roomToken;
    protected String mediaServerAddress;
    protected int mediaServerPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            mediaMode = intent.getStringExtra(Constants.INTENT_MEDIA_MODE);
            roomId = intent.getStringExtra(Constants.INTENT_ROOM_NUMBER);
            roomToken = intent.getStringExtra(Constants.INTENT_ROOM_TOKEN);
            displayName = intent.getStringExtra(Constants.INTENT_NICK_NAME);
            mediaServerAddress = intent.getStringExtra(Constants.INTENT_MEDIA_ADDRESS);
            mediaServerPort = intent.getIntExtra(Constants.INTENT_MEDIA_PORT, 3443);
        }

<<<<<<< HEAD
=======

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        currentUser = new VVUser(displayName, peerId, VVUser.ROLE_STUDENT);

        peerViewLocal = findViewById(R.id.peer_local);
        peerRemoteViewsMap = new HashMap<>();
        peerViewRemotes = new ArrayList<>();
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_0));
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_1));
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_2));
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_3));
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_4));
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_5));
        peerViewRemotes.add((PeerView) findViewById(R.id.peer_remote_6));

        // check permission
        checkAudioCameraPermission();

        // init
        shinevvClient = new Shinevv(getApplicationContext()
                , roomId            // 房间号
                , mediaMode
                , peerId
                , displayName
                , mediaServerAddress
                , mediaServerPort
                , roomToken
        );
        shinevvClient.addShinevvListener(this);
        shinevvClient.joinRoom();
        // 关闭免提
        shinevvClient.setSpeakerEnable(false);

        peerViewLocal.init(shinevvClient.getEglBaseContext());
        for (PeerView remoteView : peerViewRemotes) {
            remoteView.init(shinevvClient.getEglBaseContext());
        }

        // 摄像头控制
        ivCameraControl = findViewById(R.id.camera_control);
        vCameraSwitch = findViewById(R.id.camera_switch);
        ivPhoneControl = findViewById(R.id.phone_control);
        ivSpeakerControl = findViewById(R.id.speaker_control);

        ivCameraControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCamera();
            }
        });
        vCameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        ivPhoneControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePhone();
            }
        });
        ivSpeakerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSpeaker();
            }
        });

        if (mediaMode.equals(Shinevv.TRACK_KINE_AUDIO)) {
            enableVideo = false;
        }
        ivCameraControl.setImageResource(enableVideo ? R.mipmap.ic_camera_captuering : R.mipmap.ic_camera_enable);
        ivPhoneControl.setImageResource(R.mipmap.ic_phone_recording);

        updateRoomInfo();
    }

    @Override
    protected void onPause() {
        shinevvClient.onPause();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        shinevvClient.onResume();
    }

    @Override
    protected void onDestroy() {
        shinevvClient.dispose();
        shinevvClient.removeShinevvListener(this);
        shinevvClient = null;

        peerViewLocal.dispose();
        for (PeerView remoteView : peerViewRemotes) {
            remoteView.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onConnected() {
        // 连接成功
        Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectFail() {
        // 连接失败
        Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
<<<<<<< HEAD
    public void onRejectedPeerMax() {

    }

    @Override
=======
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
    public void onKickedOff() {
        Toast.makeText(this, "您被管理员移出房间", Toast.LENGTH_LONG).show();
        finish();
    }

    /// implement IVVListener.IVVClassListener
    @Override
    public void onClassStart(VVPeers peers, long startTime) {
        // 课程开始
        Toast.makeText(this, "class begin", Toast.LENGTH_LONG).show();
<<<<<<< HEAD
        for (VVUser vvUser : peers.getPeers()) {
=======
        for(VVUser vvUser : peers.getPeers()){
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
            onNewPeer(vvUser);
        }
    }

    @Override
    public void onClassOver() {
        // 课程结束
        Toast.makeText(this, "class end", Toast.LENGTH_LONG).show();
<<<<<<< HEAD
=======

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        peerViewLocal.onPeerClosed();
        peerRemoteViewsMap.clear();
        for (PeerView remoteView : peerViewRemotes) {
            remoteView.onPeerClosed();
        }
    }

    @Override
    public void onNewMediaPeer(VVUser vvUser) {
        onNewPeer(vvUser);
<<<<<<< HEAD
=======
        updateRoomInfo();
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
    }

    @Override
    public void onMediaPeerClose(String peerId) {
<<<<<<< HEAD
        if (peerRemoteViewsMap.containsKey(peerId)) {
            PeerView peerView = peerRemoteViewsMap.get(peerId);
            if (peerView != null) {
                peerView.onPeerClosed();
                peerRemoteViewsMap.remove(peerId);
            }
        }
=======
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        updateRoomInfo();
    }

    public void updateRoomInfo() {
<<<<<<< HEAD
        setTitle(String.format("%s - %d人 - %s - %s",
                roomId,
                peerRemoteViewsMap.size() + 1,
                (mediaMode.equals(Shinevv.TRACK_KINE_VIDEO) ? "正常模式" : "语音模式"),
                displayName
        ));
    }

=======

        setTitle(String.format("%s - %d人 - %s - %s",
                    roomId,
                    peerRemoteViewsMap.size()+1,
                    (mediaMode.equals(Shinevv.TRACK_KINE_VIDEO) ? "正常模式":"语音模式"),
                    displayName
        ));
    }


>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
    /// impl IVVListener.IVVMembersListener
    @Override
    public void onCurrentPeers(VVPeers currentPeers) {
    }

    @Override
    public void onNewPeer(VVUser vvUser) {
        // 新成员进入
<<<<<<< HEAD
        if (vvUser == null)
            return;
        if (peerRemoteViewsMap.containsKey(vvUser.getPeerId()))
            return;
        PeerView peerViewCan = null;
        for (PeerView remotePeerView : peerViewRemotes) {
            if (!remotePeerView.hasPeerInfo()) {
=======
        if(vvUser == null) return;
        if(peerRemoteViewsMap.containsKey(vvUser.getPeerId())) return;

        PeerView peerViewCan = null;
        for(PeerView remotePeerView: peerViewRemotes){
            if(!remotePeerView.hasPeerInfo()){
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
                peerViewCan = remotePeerView;
                break;
            }
        }
<<<<<<< HEAD
        if (peerViewCan != null) {
            peerViewCan.setPeerInfo(vvUser);
            peerRemoteViewsMap.put(vvUser.getPeerId(), peerViewCan);
        }
        updateRoomInfo();
=======
        if(peerViewCan!=null){
            peerViewCan.setPeerInfo(vvUser);
            peerRemoteViewsMap.put(vvUser.getPeerId(), peerViewCan);
        }
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
    }

    @Override
    public void onRemovePeer(VVUser vvUser) {
        // 成员离开
        if (peerRemoteViewsMap.containsKey(vvUser.getPeerId())) {
            PeerView peerView = peerRemoteViewsMap.get(vvUser.getPeerId());
            if (peerView != null) {
                peerView.onPeerClosed();
                peerRemoteViewsMap.remove(vvUser.getPeerId());
<<<<<<< HEAD
=======

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
                updateRoomInfo();
            }
        }
    }

<<<<<<< HEAD
    @Override
    public void onRoleChanged(VVUser vvUser) {

    }

=======
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
    /// impl IVVListener.IVVMediaListener
    @Override
    public void onCreateSessionFail(String errorDesc) {
        Toast.makeText(this, "create session fail", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddLocalVideoTrack(VideoTrack videoTrack) {
        // 本端视频回调
        peerViewLocal.setPeerInfo(currentUser);
        peerViewLocal.setMediaTrackInfo(videoTrack);
        peerViewLocal.setMirror(true);
    }

    @Override
    public void onAddRemoteVideoTrack(VideoTrack videoTrack, VVUser vvUser) {
<<<<<<< HEAD
        onNewPeer(vvUser);
=======

        onNewPeer(vvUser);

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        // 远端视频回调
        if (peerRemoteViewsMap.containsKey(vvUser.getPeerId())) {
            PeerView peerView = peerRemoteViewsMap.get(vvUser.getPeerId());
            peerView.setMediaTrackInfo(videoTrack);
            peerView.setPeerInfo(vvUser);
<<<<<<< HEAD
=======

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
            // 为了节省带宽，远端视频默认不接受，需要手动开启
            shinevvClient.pauseRemotePeerVideo(vvUser.getPeerId(), false);
        }
    }

    @Override
<<<<<<< HEAD
=======
    public void onModifyLocalAudio(boolean success) {
    }

    @Override
    public void onModifyLocalVideo(boolean success) {
    }

    @Override
    public void onVideoRejectedByServer() {
    }

    @Override
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
    public void onReceiveVideoSilent(boolean status) {
        // 收到关闭/开启视频消息
        if (status) {
            // waiting for changing state by onAddLocalVideoTrack
        } else {
            peerViewLocal.onVideoClose();
        }
    }

    @Override
    public void onReceiveAudioSilent(boolean status) {
        // 收到关闭/开启音频消息
        if (status) {
            peerViewLocal.onAudioResume();
        } else {
            peerViewLocal.onAudioPaused();
        }
    }

    @Override
    public void onRemoteVideoClose(String peerId) {
        // 成员视频关闭
        if (peerRemoteViewsMap.containsKey(peerId)) {
            PeerView peerView = peerRemoteViewsMap.get(peerId);
            if (peerView != null) {
                peerView.onVideoClose();
            }
        }
    }

    @Override
    public void onAddRemoteScreenShareTrack(VideoTrack videoTrack, VVUser vvUser) {
    }

    @Override
    public void onRemoteScreenShareClose(String peerId) {
    }

    @Override
    public void onRemoteAudioPaused(String peerId) {
        // 成员音频暂停
        if (peerRemoteViewsMap.containsKey(peerId)) {
            PeerView peerView = peerRemoteViewsMap.get(peerId);
            peerView.onAudioPaused();
        }
    }

    @Override
    public void onRemoteAudioResume(String peerId) {
        // 成员音频开启
        if (peerRemoteViewsMap.containsKey(peerId)) {
            PeerView peerView = peerRemoteViewsMap.get(peerId);
            peerView.onAudioResume();
        }
    }

    @Override
    public void onRecTransportStats(ArrayList<VVTransportInfo> transportInfos) {
        // 更新网络状态信息
<<<<<<< HEAD
        for (VVTransportInfo item : transportInfos) {
            String peerId = item.getPeerId();

            if (TextUtils.isEmpty(peerId))
                continue;

            PeerView peerView = peerRemoteViewsMap.get(peerId);
            if (peerView != null) {
=======
        for(VVTransportInfo item : transportInfos){
            String peerId = item.getPeerId();

            if(TextUtils.isEmpty(peerId)) continue;

            PeerView peerView = peerRemoteViewsMap.get(peerId);
            if(peerView!=null) {
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
                peerView.onRecTransportStats(item);
            }
        }
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
<<<<<<< HEAD
=======

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
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

    //开启或关闭视频
<<<<<<< HEAD
    private void toggleCamera() {
        enableVideo = !enableVideo;
        shinevvClient.modifyVideoStatus(enableVideo);
        ivCameraControl.setImageResource(enableVideo ? R.mipmap.ic_camera_captuering : R.mipmap.ic_camera_enable);
        if (!enableVideo) {
            peerViewLocal.onVideoClose();
        }
=======
    private void toggleCamera(){
        enableVideo = !enableVideo;
        shinevvClient.modifyVideoStatus(enableVideo);
        ivCameraControl.setImageResource(enableVideo ?  R.mipmap.ic_camera_captuering : R.mipmap.ic_camera_enable);
        if(!enableVideo){
            peerViewLocal.onVideoClose();
        }

>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        mediaMode = enableVideo ? Shinevv.TRACK_KINE_VIDEO : Shinevv.TRACK_KINE_AUDIO;
        updateRoomInfo();
    }

<<<<<<< HEAD
    private void switchCamera() {
=======
    private void switchCamera(){
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        frontCamera = !frontCamera;
        shinevvClient.switchCamera();
    }

<<<<<<< HEAD
    private void togglePhone() {
=======
    private void togglePhone(){
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        enableAudio = !enableAudio;
        shinevvClient.modifyAudioStatus(enableAudio);
        ivPhoneControl.setImageResource(enableAudio ? R.mipmap.ic_phone_recording : R.mipmap.ic_phone_enable);
    }

<<<<<<< HEAD
    private void toggleSpeaker() {
=======
    private void toggleSpeaker(){
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        enableSpeakerFree = !enableSpeakerFree;
        shinevvClient.setSpeakerEnable(enableSpeakerFree);
        ivSpeakerControl.setImageResource(enableSpeakerFree ? R.mipmap.ic_speaker_enable : R.mipmap.ic_speaker_disable);
    }
}
