package com.shinevv.vvroom.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
<<<<<<< HEAD
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
=======
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

<<<<<<< HEAD
import com.shinevv.vvroom.Shinevv;

import org.webrtc.Logging;

=======
import com.alibaba.fastjson.JSON;
import com.shinevv.vvroom.Shinevv;
import com.shinevv.vvroom.modles.VVUser;

import org.webrtc.Logging;


>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    protected EditText etRoomNumber;
    protected EditText etRoomToken;
    protected EditText etNickName;
    protected EditText etMediaServerAddress;
    protected EditText etMediaServerPort;
    protected RadioGroup rgMode;

    protected String roomId;
    protected String roomToken;
    protected String displayName;
    protected String mediaServerAddress;
    protected String mediaServerPort;
    protected boolean roleTeacher;
    protected String mediaMode = Shinevv.TRACK_KINE_VIDEO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_login);

        etRoomNumber = findViewById(R.id.room_number);
        etRoomToken = findViewById(R.id.room_token);
        etNickName = findViewById(R.id.nick_name);
        etMediaServerAddress = findViewById(R.id.media_server_address);
        etMediaServerPort = findViewById(R.id.media_server_port);
        rgMode = findViewById(R.id.media_mode);
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.audio_mode){
                    mediaMode = Shinevv.TRACK_KINE_AUDIO;
                }else if(checkedId == R.id.video_mode){
                    mediaMode = Shinevv.TRACK_KINE_VIDEO;
                }
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAction();
            }
        });

        refreshData();
    }

    protected void refreshData() {
        int num = (int) ((Math.random() * 9 + 1) * 1000);

        etRoomNumber.setText(readFromCache(Constants.INTENT_ROOM_NUMBER, "50414"));
        etRoomToken.setText(readFromCache(Constants.INTENT_ROOM_TOKEN, "b775bd9f1f1c0c89b9f9f949919cc431"));
        etNickName.setText(readFromCache(Constants.INTENT_NICK_NAME, "android-"+num));
        etMediaServerAddress.setText(readFromCache(Constants.INTENT_MEDIA_ADDRESS, "sdk.sl.shinevv.com"));
        etMediaServerPort.setText(readFromCache(Constants.INTENT_MEDIA_PORT, "3443"));
    }

    protected void LoginAction() {
        roomId = etRoomNumber.getText().toString().trim();
        if (TextUtils.isEmpty(roomId)) {
            Toast.makeText(this, "请输入房间号码", Toast.LENGTH_LONG).show();
            return;
        }
        roomToken = etRoomToken.getText().toString().trim();
        if (TextUtils.isEmpty(roomToken)) {
            Toast.makeText(this, "请输入房间Token", Toast.LENGTH_LONG).show();
            return;
        }
        displayName = etNickName.getText().toString().trim();
        if (TextUtils.isEmpty(displayName)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_LONG).show();
            return;
        }
        mediaServerAddress = etMediaServerAddress.getText().toString().trim();
        if (TextUtils.isEmpty(mediaServerAddress)) {
            Toast.makeText(this, "请输入服务器地址", Toast.LENGTH_LONG).show();
            return;
        }
        mediaServerPort = etMediaServerPort.getText().toString().trim();
        int portNo = 0;
        try{
            portNo = Integer.parseInt(mediaServerPort);
            if (portNo < 0 || portNo > 65535) {
                Toast.makeText(this, "请输入服务器端口，默认3443", Toast.LENGTH_LONG).show();
                return;
            }
        }catch (Exception e){
            Toast.makeText(this, "请输入服务器端口，默认3443", Toast.LENGTH_LONG).show();
            return;
        }

        writeToCache(Constants.INTENT_ROOM_NUMBER, roomId);
        writeToCache(Constants.INTENT_ROOM_TOKEN, roomToken);
        writeToCache(Constants.INTENT_NICK_NAME, displayName);
        writeToCache(Constants.INTENT_MEDIA_ADDRESS, mediaServerAddress);
        writeToCache(Constants.INTENT_MEDIA_PORT, mediaServerPort);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.INTENT_ORIENTATION, getScreenOrientation());
        intent.putExtra(Constants.INTENT_ROOM_NUMBER, roomId);
        intent.putExtra(Constants.INTENT_ROOM_TOKEN, roomToken);
        intent.putExtra(Constants.INTENT_NICK_NAME, displayName);
        intent.putExtra(Constants.INTENT_MEDIA_ADDRESS, mediaServerAddress);
        intent.putExtra(Constants.INTENT_MEDIA_PORT, portNo);
        intent.putExtra(Constants.INTENT_MEDIA_MODE, mediaMode);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Logging.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Logging.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public String readFromCache(String cacheKey, String defaultValue){
        SharedPreferences pre = getSharedPreferences("VVSDK-demo", Context.MODE_PRIVATE);
        return pre.getString(cacheKey, defaultValue);
    }

    public void writeToCache(String cacheKey, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("VVSDK-demo", Context.MODE_PRIVATE).edit();
        editor.putString(cacheKey, value);
        editor.apply();
        editor.commit();
    }

}
