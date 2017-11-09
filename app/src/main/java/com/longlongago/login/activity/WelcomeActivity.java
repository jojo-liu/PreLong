package com.longlongago.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.longlongago.Api;
import com.longlongago.login.model.KeyConstance;
import com.longlongago.login.model.UserPreference;
import com.longlongago.openvcall.LLAApplication;
import com.longlongago.openvcall.R;
import com.longlongago.resideMenu.MenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jojo on 24/07/2017.
 */

public class WelcomeActivity extends Activity {
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);

        iv = (ImageView) this.findViewById(R.id.logo);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(2000);
        iv.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            /**
             * 动画结束时判断是否保存有登录的信息
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                //暂时用用户名密码登录
                String username = UserPreference.read(KeyConstance.IS_USER_ACCOUNT, null);//软件还没有保持账号
                String password = UserPreference.read(KeyConstance.IS_USER_PASSWORD, null);
                String userId = UserPreference.read(KeyConstance.IS_USER_ID, null);
                String isLogin = UserPreference.read(KeyConstance.IS_LOGIN_KEY, null);

                if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(username) || "false".equals(isLogin) || TextUtils.isEmpty(isLogin)) {//没有保存的登录信息跳转到登录界面
                    //空的，表示没有注册，或者清除数据
                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                } else {
                    //用保存的信息直接登录

                    HashMap<String,String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    String url = Api.SERVER_ROOT + "/api/user/signIn";
                    RequestQueue requestQueue = Volley.newRequestQueue(WelcomeActivity.this);
                    JsonObjectRequest jsonRequest = new JsonObjectRequest(
                            Request.Method.POST, url,
                            new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("flag", "------->" + response.toString());
                                    try {
                                        String code = response.getString("code");
                                        if (code.equals("200")) {
//                                            Toast.makeText(WelcomeActivity.this, "登录成功!", Toast.LENGTH_LONG ).show();
                                            Intent intent = new Intent(WelcomeActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                        } else {
//                                            Toast.makeText(WelcomeActivity.this, "用户名或者密码错误", Toast.LENGTH_LONG ).show();
                                            Intent intent = new Intent();
                                            intent.setClass(WelcomeActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("flag", "请求数据失败");
                                    Intent intent = new Intent();
                                    intent.setClass(WelcomeActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                    finish();
                                }
                            }) {
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        LLAApplication.getInstance().exit();
    }
}
