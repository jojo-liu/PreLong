package com.longlongago.userInterface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import com.longlongago.login.model.UserPreference;
import com.longlongago.openvcall.LLAApplication;
import com.longlongago.openvcall.R;
import com.longlongago.openvcall.ui.ChatActivity;
import com.longlongago.openvcall.ui.CreateActivity;
import com.longlongago.resideMenu.MenuActivity;
import com.longlongago.roomManager.model.Room;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Jojo on 27/07/2017.
 */

public class RoomActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RoomActivity";

    private Button buttonChoose;
    private Button buttonUpload;

    private ImageView imageView;

    private EditText roomName;

    private Bitmap bitmap;

    private RadioGroup roomNo;

    private Integer peopleNo;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = Api.SERVER_ROOT + "/api/room/createRoom";

//    private String KEY_IMAGE = "avatar";
//    private String KEY_NAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        roomNo = (RadioGroup) findViewById(R.id.roomNo);

        roomName = (EditText) findViewById(R.id.roomName);

        imageView  = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        roomNo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                peopleNo = 3;
                if(checkedId == R.id.two) {
                    peopleNo = 2;
                }
                else {
                    if (checkedId == R.id.four) {
                        peopleNo = 4;
                    }
                }
            }
        });
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
        if(bitmap == null) {
            Resources res = getResources();
            bitmap = BitmapFactory.decodeResource(res, R.drawable.room_pic);
        }
        String roomPic = getStringImage(bitmap);

        if(TextUtils.isEmpty(roomName.getText().toString())) {
            roomName.setText(R.string.default_room_name);
        }
        //Creating parameters
        Map<String,String> params = new Hashtable<String, String>();

        //Adding parameters
        String userId = UserPreference.read(KeyConstance.IS_USER_ID, null);
        params.put("userId", userId);
        params.put("peopleNo", peopleNo.toString());
        params.put("roomPic", roomPic);
        params.put("roomName", roomName.getText().toString().trim());

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
                                Toast.makeText(RoomActivity.this, R.string.create_room_successfully, Toast.LENGTH_LONG ).show();
                                Gson gson = new Gson();
                                Room room = gson.fromJson(response.getString("content"), new TypeToken<Room>() {}.getType());
                                Intent intent = new Intent(RoomActivity.this, ChatActivity.class);
                                intent.putExtra("roomId", room.getRoomId());
                                intent.putExtra("roomName", room.getRoomName());
//                                intent.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, room.getRoomId());
//                                intent.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(com.longlongago.openvcall.R.array.encryption_mode_values)[0]);
                                Log.i("current channel name", room.getRoomId());
//                                Log.i("current encryption key", room.getRoomId());
//                                Log.i("current mode", getResources().getStringArray(com.longlongago.openvcall.R.array.encryption_mode_values)[0]);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d(TAG, "------->" + code);
                                Toast.makeText(RoomActivity.this, R.string.create_room_unsuccessfully, Toast.LENGTH_LONG ).show();
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
                        Toast.makeText(RoomActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
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
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);

                String avatar = getStringImage(bitmap);
//                Log.d(TAG, "--------->image: " + avatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == buttonChoose){
            showFileChooser();
        }

        if(v == buttonUpload){
            uploadImage();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(RoomActivity.this, MenuActivity.class);
        startActivity(i);
    }
}
