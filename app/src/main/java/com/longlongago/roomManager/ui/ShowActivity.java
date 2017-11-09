package com.longlongago.roomManager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.openvcall.LLAApplication;
import com.longlongago.openvcall.R;
import com.longlongago.openvcall.ui.CreateActivity;
import com.longlongago.resideMenu.MenuActivity;
import com.longlongago.roomManager.model.Room;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.longlongago.Api.SERVER_ROOT;


public class ShowActivity extends FragmentActivity {

    private static final String TAG = "ShowActivity";

    private String URL = SERVER_ROOT + "/api/user/getUsersInRoom";

    private String defaultAvator = "assets://default_avator.jpg";

    private TextView indicatorTv;
    private View positionView;
    private ViewPager viewPager;
    private List<CommonFragment> fragments = new ArrayList<>(); // 供ViewPager使用
    private final String[] imageArray = { defaultAvator, defaultAvator, defaultAvator, SERVER_ROOT + defaultAvator, defaultAvator};

//    List<List<UserBaseInfo>> users = new ArrayList<>();

//    private List<RoomActivity> rooms = new ArrayList<>();//存储当前最新的房间信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        //add current activity into the container of LLAApplication
        LLAApplication.getInstance().addActivity(this);

        // 1. 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow()
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        positionView = findViewById(R.id.position_view);
        dealStatusBar(); // 调整状态栏高度

        //receive room information from UserInterfaceActivity
        List<Room> rooms = Parcels.unwrap(getIntent().getParcelableExtra("rooms"));

        List<List<UserBaseInfo>> users = Parcels.unwrap(getIntent().getParcelableExtra("users"));

        // 2. 初始化ImageLoader
        initImageLoader();


        // 3. 填充ViewPager
        fillViewPager(rooms, users);
    }

    /**
     * 填充ViewPager
     * @param rooms
     */
    private void fillViewPager(List<Room> rooms, List<List<UserBaseInfo>> users) {
        Log.d(TAG, "ShowRooms");
        Log.d(TAG, "rooms size: " + rooms.size());
        Log.d(TAG, "users size: " + users.size());
        indicatorTv = (TextView) findViewById(R.id.indicator_tv);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // 1. viewPager添加parallax效果，使用PageTransformer就足够了
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));

        // 2. viewPager添加adapter
        for (int i = 0; i < rooms.size(); i++) {
            fragments.add(new CommonFragment());

//            //Creating a Request Queue
//            RequestQueue requestQueue = Volley.newRequestQueue(ShowActivity.this);
//            //get all user avatars here and initialize them in fragment
//            //get all user info in each room
//            //Creating parameters
//            Map<String,String> params = new Hashtable<String, String>();
//            //Adding parameters
//            params.put("roomId", rooms.get(i).getRoomId());
//
//            int finalI = i;
//            JsonObjectRequest jsonRequest = new JsonObjectRequest(
//                    Request.Method.POST, URL,
//                    new JSONObject(params), new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Log.i("roomId", "------->" + rooms.get(finalI).getRoomId());
//                    Log.i("flag", "------->" + response.toString());
//                    Log.i("roomInfo", "------->" + rooms.get(finalI).toString());
//                    try {
//                        String code = response.getString("code");
//                        if (code.equals("200")) {
//                            Gson gson = new Gson();
//                            List<UserBaseInfo> temp = gson.fromJson(response.getString("content"), new TypeToken<ArrayList<UserBaseInfo>>() {}.getType());
//                            users.add(temp);
//                            Log.d(TAG, "------->" + users.get(finalI).size());
//                        } else {
//                            Log.d(TAG, "------->" + code);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    Log.d("flag", "请求数据失败" + volleyError.getMessage().toString());
//                    Toast.makeText(ShowActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
//                }
//            }){
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("Accept", "application/json");
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//            };
//            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 2, 1.0f));
//            //Adding request to the queue
//            requestQueue.add(jsonRequest);
//            requestQueue.start();
        }

        //viewPager是一个view，需要通过适配器获取数据源
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "------->position: " + position);
                Log.d(TAG, "------->users.size(): " + users.get(position % users.size()));
                CommonFragment fragment = fragments.get(position % rooms.size());
                fragment.bindData(rooms.get(position % rooms.size()).getRoomPicUrl(), users.get(position % users.size()), rooms.get(position % rooms.size()).getRoomName(), rooms.get(position % rooms.size()).getRoomId());
                return fragment;

//                List<String> userAvatars = new ArrayList<String>();
//                for(int i = 0; i < users.get(position % rooms.size()).size(); i++) {
//                    userAvatars.add(users.get(position % rooms.size()).get(i).getAvatarUrl());
//                }
//                fragment.bindData(imageArray[position % imageArray.length],
//                        rooms.get(position % rooms.size()).getRoomName(), userAvatars);
//                return fragment;

//                //get all user avatars here and initialize them in fragment
//                //get all user info in each room
//                //Creating a Request Queue
//                RequestQueue requestQueue = Volley.newRequestQueue(ShowActivity.this);
//                //Creating parameters
//                Map<String,String> params = new Hashtable<String, String>();
//                //Adding parameters
//                params.put("roomId", rooms.get(position % rooms.size()).getRoomId());
//
//                JsonObjectRequest jsonRequest = new JsonObjectRequest(
//                        Request.Method.POST, URL,
//                        new JSONObject(params), new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("roomId", "------->" + rooms.get(position % rooms.size()).getRoomId());
//                        Log.d("flag", "------->" + response.toString());
//                        Log.d("roomInfo", "------->" + rooms.get(position % rooms.size()).toString());
//                        try {
//                            String code = response.getString("code");
//                            if (code.equals("200")) {
////				        		Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG ).show();
//                                Gson gson = new Gson();
//                                List<UserBaseInfo> userInRoom = gson.fromJson(response.getString("content"), new TypeToken<ArrayList<UserBaseInfo>>() {}.getType());
//                                if(userInRoom.isEmpty()) {
//                                    Log.d(TAG, "------->" + "当前房间为空");
//                                }
//                                Log.d(TAG, "------->" + userInRoom.size());
//                                List<String> userAvatars = new ArrayList<String>();
//                                for(int i = 0; i < userInRoom.size(); i++) {
//                                    userAvatars.add(userInRoom.get(i).getAvatarUrl());
//                                }
//                                fragment.bindData(imageArray[position % imageArray.length],
//                                        rooms.get(position % rooms.size()).getRoomName(), userAvatars);
//                            } else {
//                                Log.d(TAG, "------->" + code);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Showing toast
//                        Toast.makeText(ShowActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
//                    }
//                }){
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("Accept", "application/json");
//                        headers.put("Content-Type", "application/json");
//                        return headers;
//                    }
//                };
//                //Adding request to the queue
//                requestQueue.add(jsonRequest);
//                return fragment;
            }

            @Override
            public int getCount() {
                return rooms.size();
            }
        });


        // 3. viewPager滑动时，调整指示器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                updateIndicatorTv();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        updateIndicatorTv();
    }

    /**
     * 更新指示器
     */
    private void updateIndicatorTv() {
        int totalNum = viewPager.getAdapter().getCount();
        int currentItem = viewPager.getCurrentItem() + 1;
        indicatorTv.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + totalNum));
    }

    /**
     * 调整沉浸式菜单的title
     */
    private void dealStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = positionView.getLayoutParams();
            lp.height = statusBarHeight;
            positionView.setLayoutParams(lp);
        }
    }

    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    @SuppressWarnings("deprecation")
    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(480, 800)
                // default = device screen dimensions
                .threadPoolSize(1)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13) // default
                .discCacheSize(50 * 1024 * 1024) // 缓冲大小
                .discCacheFileCount(100) // 缓冲文件数目
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();

        // 2.单例ImageLoader类的初始化
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ShowActivity.this, MenuActivity.class);
        startActivity(i);
    }
}
