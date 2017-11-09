package com.longlongago.openvcall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.longlongago.resideMenu.MenuActivity;
import com.longlongago.roomManager.model.Room;
import com.longlongago.roomManager.ui.ShowActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jojo on 06/09/2017.
 */

public class JoinActivity extends BaseActivity{
    private static final String TAG = "JoinActivity";

    List<Room> rooms = new ArrayList<>();

    List<List<UserBaseInfo>> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);
//        setContentView(R.layout.home);
        forwardToJoinRoom();
    }

    @Override
    protected void initUIandEvent() {

    }

    @Override
    protected void deInitUIandEvent() {

    }

    public void forwardToJoinRoom() {
        Log.d(TAG, "GetRooms");

        String userId = UserPreference.read(KeyConstance.IS_USER_ID, null);
        HashMap<String,String> params = new HashMap<>();
//        Log.d("flag", "---->" + userId);
        params.put("userId", userId);
        String url = Api.SERVER_ROOT + "/api/room/getRooms";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, url,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d("flag", "------->" + response.toString());
                try {
                    String code = response.getString("code");
                    if (code.equals("200")) {
//				        		Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG ).show();
                        Gson gson = new Gson();

//                         Log.d("List<room>:", response.getString("content"));
                        rooms = gson.fromJson(response.getString("content"), new TypeToken<ArrayList<Room>>() {}.getType());
                        if(rooms.isEmpty()) {
                            Toast.makeText(JoinActivity.this, R.string.no_active_room, Toast.LENGTH_LONG ).show();
                            Intent i = new Intent(JoinActivity.this, MenuActivity.class);
                            startActivity(i);
                        }
//                        Log.d(TAG, "------->" + rooms.size());
                        collectUsersInRoom(rooms);
//                        Intent intent = new Intent(MainActivity.this, ShowActivity.class);
//                        intent.putExtra("rooms", Parcels.wrap(rooms));
//                        startActivity(intent);
                    } else {
                        Toast.makeText(JoinActivity.this, R.string.no_response_from_server, Toast.LENGTH_LONG ).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("flag", "请求数据失败");
                Toast.makeText(JoinActivity.this, R.string.no_response_from_server, Toast.LENGTH_LONG ).show();
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

//        collectUsersInRoom(rooms);

    }

    public void collectUsersInRoom(List<Room> rooms) {
        Log.d(TAG, "Collect Users in Certain Room");
        for(int i = 0; i < rooms.size(); i++) {
            HashMap<String,String> params = new HashMap<>();
            params.put("roomId", rooms.get(i).getRoomId());
            String url = Api.SERVER_ROOT + "/api/user/getUsersInRoom";
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST, url,
                    new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                Log.d("flag", "------->" + response.toString());
                    try {
                        String code = response.getString("code");
                        if (code.equals("200")) {
//				        		Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG ).show();
                            Gson gson = new Gson();

//                         Log.d("List<room>:", response.getString("content"));
                            List<UserBaseInfo> temp = (gson.fromJson(response.getString("content"), new TypeToken<ArrayList<UserBaseInfo>>() {}.getType()));
                            users.add(temp);
                            if(temp.isEmpty()) {
                                Log.d(TAG, "------->" + "当前房间为空" + finalI);
                                Toast.makeText(JoinActivity.this, R.string.no_active_room, Toast.LENGTH_LONG ).show();
                            }
                            if(users.size() == rooms.size()) {
                                transferToShowActivity(rooms, users);
                            }
                        } else {
                            Log.d(TAG, "------->" + "当前没有活跃房间");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("flag", "请求数据失败");
//                    Toast.makeText(MainActivity.this, "服务器无响应!", Toast.LENGTH_LONG ).show();
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

//        transferToShowActivity(rooms, users);
    }

    public void transferToShowActivity(List<Room> rooms, List<List<UserBaseInfo>> users) {
        Intent intent = new Intent(JoinActivity.this, ShowActivity.class);
        intent.putExtra("rooms", Parcels.wrap(rooms));
        intent.putExtra("users", Parcels.wrap(users));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(JoinActivity.this, MenuActivity.class);
        startActivity(i);
    }
}
