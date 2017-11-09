package com.longlongago.resideMenu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.longlongago.login.activity.LoginActivity;
import com.longlongago.login.activity.WelcomeActivity;
import com.longlongago.login.model.KeyConstance;
import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.login.model.UserPreference;
import com.longlongago.openvcall.LLAApplication;
import com.longlongago.openvcall.R;
import com.longlongago.openvcall.ui.CreateActivity;
import com.longlongago.resideMenu.model.CircleAvatar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * User: special
 * Date: 13-12-22
 * Time: 下午1:31
 * Mail: specialcyci@gmail.com
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

//    private ImageView userAvatar;
    private CircleAvatar userAvatar;

    private TextView username;

    private EditText userSignature;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = Api.SERVER_ROOT + "/api/user/uploadAvatar";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);

        username = (TextView) findViewById(R.id.username);
        String usernameInfo = UserPreference.read(KeyConstance.IS_USER_ACCOUNT, null);
        if(TextUtils.isEmpty(usernameInfo)) {
            Toast.makeText(ProfileActivity.this, "当前用户信息错误", Toast.LENGTH_LONG ).show();
            Intent intent = new Intent();
            intent.setClass(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        } else {
            username.setText(UserPreference.read(KeyConstance.IS_USER_ACCOUNT, null));
        }
        userSignature = (EditText) findViewById(R.id.signature);

        userAvatar  = (CircleAvatar) findViewById(R.id.avatar);
        String username = UserPreference.read(KeyConstance.IS_USER_ACCOUNT, null);
        String isLogin = UserPreference.read(KeyConstance.IS_LOGIN_KEY, null);
        if (TextUtils.isEmpty(username) || "false".equals(isLogin) || TextUtils.isEmpty(isLogin)) {//没有保存的登录信息跳转到登录界面
            //空的，表示没有注册，或者清除数据
            Intent intent = new Intent();
            intent.setClass(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        } else {
            collectCurrentUserAvator(username);
        }


        userAvatar.setOnClickListener(this);
//        userSignature.setOnClickListener(this);
    }

    private void collectCurrentUserAvator(String username) {
        Log.d(TAG, "Collect Current User Avatar");
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ProfileActivity.this));
        HashMap<String,String> params = new HashMap<>();
        params.put("username", username);
        String url = Api.SERVER_ROOT + "/api/user/getUserInfo";
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST, url,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d("flag", "------->" + response.toString());
                try {
                    String code = response.getString("code");
                    if (code.equals("200")) {
                        Gson gson = new Gson();
                        UserBaseInfo userBaseInfo = (gson.fromJson(response.getString("content"), UserBaseInfo.class));
                        ImageLoader.getInstance().displayImage(Api.SERVER_ROOT + userBaseInfo.getAvatarUrl(), userAvatar);
                        userSignature.setText(userBaseInfo.getSignature());
                    } else {
                        Log.d(TAG, "------->" + "用户信息错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("flag", "请求数据失败");
                Toast.makeText(ProfileActivity.this, "服务器无响应!", Toast.LENGTH_LONG ).show();
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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        //Converting Bitmap to String
        String avatar = getStringImage(bitmap);

//        //Getting Image Name
//        String username = editTextName.getText().toString().trim();

        String signatureInfo = userSignature.getText().toString().trim();
        userSignature.setText(signatureInfo);

        //Creating parameters
        Map<String,String> params = new Hashtable<String, String>();

        //Adding parameters
        params.put("avatar", avatar);
        String userId = UserPreference.read(KeyConstance.IS_USER_ID, null);
        params.put("userId", userId);
        params.put("signature", signatureInfo);

//        Log.d(TAG, "--------->image: " + avatar);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        try {
//							Response result = new Gson().fromJson(response.toString(), CustomClass.class);
                            String code = response.getString("code");
                            if (code.equals("200")) {
                                Toast.makeText(ProfileActivity.this, R.string.upload_avator_successfully, Toast.LENGTH_LONG ).show();
                            } else {
                                Log.d(TAG, "------->" + code);
                                Toast.makeText(ProfileActivity.this, R.string.upload_avator_unsuccessfully, Toast.LENGTH_LONG ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(ProfileActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(jsonRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userAvatar.setImageBitmap(bitmap);

                uploadImage();
//                Log.d(TAG, "--------->image: " + avatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == userAvatar){
            showFileChooser();
        }

//        if(v == userSignature){
//            userSignature.setText(userSignature.getText().toString().trim());
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ProfileActivity.this, MenuActivity.class);
        startActivity(i);
    }

}
