package com.shinevv.vvroom.demo1v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_VIDEO = 1;

    private ListView memberListView;
    private ArrayAdapter<String> memberAdapter;
    private ArrayList<String> memberList = new ArrayList<String>();

    private String mUsername;
    private Socket mSocket;

    PopupWindow popupWindow2;
    public int currentRoomId = 0;
    public int currentMethod = 0;   //当前呼叫方式，0 - 视频通话，1 - 语音通话

    private Boolean isConnected = true;
    private  WindowManager.LayoutParams lp;

    public MainFragment() {
        super();
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);

        BaseApplication app = (BaseApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("calling", onCalling);
        mSocket.on("call agree", onCallAgree);
        mSocket.on("call end", onCallEnd);
        mSocket.connect();

        startSignIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("calling", onCalling);
        mSocket.off("call agree", onCallAgree);
        mSocket.off("call end", onCallEnd);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        memberListView = (ListView) view.findViewById(R.id.list_members);
        memberAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, memberList){
            class ViewHolder {
                TextView userName;
                TextView videoCall;
                TextView audioCall;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;

                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_member, null);
                    holder = new ViewHolder();

                    holder.userName = (TextView) convertView.findViewById(R.id.tv_username);
                    holder.videoCall = (TextView) convertView.findViewById(R.id.btn_video_call);
                    holder.audioCall = (TextView) convertView.findViewById(R.id.btn_audio_call);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                final String username = (String) getItem(position);
                holder.userName.setText(username);

                holder.videoCall.setVisibility((username.equals(mUsername))? View.GONE : View.VISIBLE );
                holder.audioCall.setVisibility((username.equals(mUsername))? View.GONE : View.VISIBLE );
                holder.userName.setTextColor((username.equals(mUsername))? Color.parseColor("#fa6565"):Color.parseColor("#1e1e1e"));

                holder.videoCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentRoomId = new Random().nextInt(90000) + 10001;
                        currentMethod = 0;

                        mSocket.emit("call",  username, currentMethod, currentRoomId);
                        showWaitPopupWindow(true, currentMethod, currentRoomId);
                    }
                });

                holder.audioCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentRoomId = new Random().nextInt(90000) + 10001;
                        currentMethod = 1;

                        mSocket.emit("call", username, currentMethod, currentRoomId);
                        showWaitPopupWindow(true, currentMethod, currentRoomId);
                    }
                });

                return convertView;
            }
        };

        memberListView.setAdapter(memberAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN) {
            if (Activity.RESULT_OK != resultCode) {
                getActivity().finish();
                return;
            }

            mUsername = data.getStringExtra("username");
            int numUsers = data.getIntExtra("numUsers", 1);
            if (numUsers > 1) {
                memberList.clear();
                memberList.addAll(data.getStringArrayListExtra("userList"));
            } else {
                memberList.add(mUsername);
            }
            memberAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_VIDEO) {
            mSocket.emit("call end", currentRoomId);
        }
    }

    private void startSignIn() {
        mUsername = null;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "connected");
                    isConnected = true;
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnected");
                    isConnected = false;
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onCalling = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String sourceUser,targetUser;
                    int method=0;
                    int roomId=0;
                    try {
                        sourceUser = data.getString("calling");
                        targetUser = data.getString("called");
                        method = data.getInt("method");
                        roomId = data.getInt("room");

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    // 判断是否当前用户的消息
                    if (targetUser.compareTo(mUsername) == 0) {
                        Toast.makeText(getActivity(), sourceUser+" 请求通话", Toast.LENGTH_LONG).show();
                        currentMethod = method;
                        currentRoomId = roomId;
                        showWaitPopupWindow(false, method, roomId);
                    }
                }
            });
        }
    };

    private Emitter.Listener onCallAgree = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int roomId=0;
                    try {
                        roomId = data.getInt("room");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    // 判断是否当前用户的消息
                    if (roomId == currentRoomId) {
                        if (popupWindow2 != null) {
                            popupWindow2.dismiss();
                        }
                        Intent intent = new Intent(getActivity(), VideoActivity.class);
                        intent.putExtra("type", (currentMethod == 0) ? "video" : "audio");
                        intent.putExtra("userName", mUsername);
                        intent.putExtra("roomId", roomId+"");
                        getActivity().startActivityForResult(intent, REQUEST_VIDEO);
                    }
                }
            });
        }
    };

    private Emitter.Listener onCallEnd = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    int roomId=0;
                    try {
                        roomId = data.getInt("room");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    // 判断是否当前用户的消息
                    if (roomId == currentRoomId) {
                        if (popupWindow2 != null) {
                            popupWindow2.dismiss();
                            lp.alpha = 1f;
                            getActivity().getWindow().setAttributes(lp);
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    boolean isExist = false;
                    for(String user: memberList){
                        if(user == username){
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        memberList.add(username);
                        memberAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    memberList.remove(username);
                    memberAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private void showWaitPopupWindow(boolean hideAcceptButton, final int method, final  int roomId){
        View popupWindow = View.inflate(getActivity(), R.layout.wait_popupwindow,null);
        ImageView jiePhone = popupWindow.findViewById(R.id.jie_Phone);
        if (hideAcceptButton) {
            //发起呼叫时，隐藏接听按钮
            jiePhone.setVisibility(View.GONE);
        }
        ImageView guaPhone = popupWindow.findViewById(R.id.gua_phone);
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        popupWindow2 = new PopupWindow(popupWindow,weight,height);
        popupWindow2.setFocusable(true);
        lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        //点击外部popueWindow消失
        popupWindow2.setOutsideTouchable(true);
        //接電話
        jiePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSocket.emit("call agree", roomId);

                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("type", (method == 0) ? "video" : "audio");
                intent.putExtra("userName", mUsername);
                intent.putExtra("roomId", roomId+"");
                getActivity().startActivityForResult(intent, REQUEST_VIDEO);

                popupWindow2.dismiss();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        //挂電話
        guaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("call end", roomId);

                popupWindow2.dismiss();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });


        getActivity().getWindow().setAttributes(lp);
        popupWindow2.showAtLocation(popupWindow, Gravity.BOTTOM,0,50);
    }
}

