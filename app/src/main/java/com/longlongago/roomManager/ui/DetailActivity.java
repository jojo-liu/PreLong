package com.longlongago.roomManager.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longlongago.Api;
import com.longlongago.login.model.KeyConstance;
import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.login.model.UserPreference;
import com.longlongago.openvcall.LLAApplication;
import com.longlongago.openvcall.R;
import com.longlongago.openvcall.ui.ChatActivity;
import com.longlongago.openvcall.ui.CreateActivity;
import com.longlongago.resideMenu.MenuActivity;
import com.longlongago.resideMenu.model.CircleAvatar;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xmuSistone on 2016/9/19.
 */
public class DetailActivity extends FragmentActivity {

    private static final String TAG = "DetailActivity";

    public static final String EXTRA_IMAGE_URL = "detailImageUrl";

    public static final String USER_INFO_IN_ROOM = "userInfoInRoom";

    public static final String IMAGE_TRANSITION_NAME = "transitionImage";

    public static final String ROOMID_TRANSITION_NAME = "roomId";
    public static final String ROOMNAME_TRANSITION_NAME = "roomName";
    public static final String HEAD1_TRANSITION_NAME = "head1";
    public static final String HEAD2_TRANSITION_NAME = "head2";
    public static final String HEAD3_TRANSITION_NAME = "head3";
    public static final String HEAD4_TRANSITION_NAME = "head4";

    private ImageView imageView;
    private TextView roomName;
    private String roomNameInfo;
    private Button joinRoom;
    private List<UserBaseInfo> userInfoInRoom;

    private String defaultAvator = "assets://default_avator.jpg";

    private LinearLayout listContainer;
    private static final String[] headStrs = {HEAD1_TRANSITION_NAME, HEAD2_TRANSITION_NAME, HEAD3_TRANSITION_NAME, HEAD4_TRANSITION_NAME};
    private static final int[] imageIds = {R.drawable.default_avator, R.drawable.default_avator, R.drawable.default_avator, R.drawable.default_avator};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);

        imageView = (ImageView) findViewById(R.id.image);
        roomName = (TextView) findViewById(R.id.roomName);
        listContainer = (LinearLayout) findViewById(R.id.detail_list_container);
        joinRoom = (Button) findViewById(R.id.button_join_room);
        joinRoom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userId = UserPreference.read(KeyConstance.IS_USER_ID, null);
                HashMap<String,String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("roomId", getIntent().getStringExtra(ROOMID_TRANSITION_NAME));
                String url = Api.SERVER_ROOT + "/api/room/joinRoom";
                RequestQueue requestQueue = Volley.newRequestQueue(DetailActivity.this);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(
                        Request.Method.POST, url,
                        new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String code = response.getString("code");
                            if (code.equals("200")) {
                                Intent i = new Intent(DetailActivity.this, ChatActivity.class);
                                i.putExtra("roomId", userInfoInRoom.get(0).getRoomId());
                                i.putExtra("roomName", roomNameInfo);
//                                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, userInfoInRoom.get(0).getRoomId());
//                                i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(com.longlongago.openvcall.R.array.encryption_mode_values)[0]);
                                Log.i("current channel name", userInfoInRoom.get(0).getRoomId());
//                                Log.i("current encryption key", userInfoInRoom.get(0).getRoomId());
//                                Log.i("current mode", getResources().getStringArray(com.longlongago.openvcall.R.array.encryption_mode_values)[0]);
                                startActivity(i);
                            } else if(code.equals("10013")) {
                                Log.d(TAG, "room is full");
                                Toast.makeText(DetailActivity.this, R.string.room_occupied, Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG, "cannot Join Current Room");
                                Toast.makeText(DetailActivity.this, R.string.join_room_unsuccessfully, Toast.LENGTH_LONG ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("flag", "请求数据失败");
                        Toast.makeText(DetailActivity.this, R.string.no_response_from_server, Toast.LENGTH_LONG ).show();
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                requestQueue.add(jsonRequest);
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        ImageLoader.getInstance().displayImage(imageUrl, imageView);

        userInfoInRoom = Parcels.unwrap(getIntent().getParcelableExtra(USER_INFO_IN_ROOM));
        roomNameInfo = getIntent().getStringExtra(ROOMNAME_TRANSITION_NAME);
        roomName.setText(roomNameInfo);

        ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);
        ViewCompat.setTransitionName(roomName, ROOMNAME_TRANSITION_NAME);

        dealListView();
    }

    private void dealListView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (int i = 0; i < userInfoInRoom.size() && 4 >= userInfoInRoom.size(); i++) {
            View childView = layoutInflater.inflate(R.layout.detail_list_item, null);
            listContainer.addView(childView);
            TextView userName = (TextView) childView.findViewById(R.id.userName);
            TextView signature = (TextView) childView.findViewById(R.id.signature);
            userName.setText(userInfoInRoom.get(i).getUsername());
            if(TextUtils.isEmpty(userInfoInRoom.get(i).getSignature())) {
                signature.setText(R.string.default_signature);
            } else {
                signature.setText(userInfoInRoom.get(i).getSignature());
            }
            CircleAvatar headView = (CircleAvatar) childView.findViewById(R.id.head);
            if(TextUtils.isEmpty(userInfoInRoom.get(i).getAvatarUrl())) {
                ImageLoader.getInstance().displayImage(defaultAvator, headView);
            } else {
                ImageLoader.getInstance().displayImage(Api.SERVER_ROOT + userInfoInRoom.get(i).getAvatarUrl(), headView);
            }
            ViewCompat.setTransitionName(headView, headStrs[i]);
        }
    }
}
