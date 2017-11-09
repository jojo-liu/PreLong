package com.longlongago.roomManager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longlongago.Api;
import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.openvcall.R;
import com.longlongago.resideMenu.model.CircleAvatar;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener{
    private ImageView imageView;
    private String imageUrl;
    private TextView roomName;
    private String roomNameInfo;
    private String roomId;
    private CircleAvatar head1, head2, head3, head4;
    private List<String> userAvatars = new ArrayList<>();
    private List<UserBaseInfo> userInfoInRoom = new ArrayList<>();

    private String defaultAvator = "assets://default_avator.jpg";
    private static final String TAG = "CommonFragment";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.image);
        if(imageUrl == null) {
            imageUrl = "assets://default_avator.jpg";
        }
        else {
            imageUrl = Api.SERVER_ROOT + imageUrl;
        }
        ImageLoader.getInstance().displayImage(imageUrl, imageView);

        roomName = (TextView) dragLayout.findViewById(R.id.roomName);
        roomName.setText(roomNameInfo);

        head1 = (CircleAvatar) dragLayout.findViewById(R.id.head1);
        if(1 <= userAvatars.size()) {
            if(TextUtils.isEmpty(userAvatars.get(0))) {
                ImageLoader.getInstance().displayImage(defaultAvator, head1);
            } else {
                ImageLoader.getInstance().displayImage(Api.SERVER_ROOT + userAvatars.get(0).toString(), head1);
            }

        }
        head2 = (CircleAvatar) dragLayout.findViewById(R.id.head2);
        if(2 <= userAvatars.size()) {
            if(TextUtils.isEmpty(userAvatars.get(1))) {
                ImageLoader.getInstance().displayImage(defaultAvator, head2);
            } else {
                ImageLoader.getInstance().displayImage(Api.SERVER_ROOT + userAvatars.get(1).toString(), head2);
            }
        }
        head3 = (CircleAvatar) dragLayout.findViewById(R.id.head3);
        if(3 <= userAvatars.size()) {
            if(TextUtils.isEmpty(userAvatars.get(2))) {
                ImageLoader.getInstance().displayImage(defaultAvator, head3);
            } else {
                ImageLoader.getInstance().displayImage(Api.SERVER_ROOT + userAvatars.get(2).toString(), head3);
            }
        }
        head4 = (CircleAvatar) dragLayout.findViewById(R.id.head4);
        if(4 <= userAvatars.size()) {
            if(TextUtils.isEmpty(userAvatars.get(3))) {
                ImageLoader.getInstance().displayImage(defaultAvator, head4);
            } else {
                ImageLoader.getInstance().displayImage(Api.SERVER_ROOT + userAvatars.get(3).toString(), head4);
            }
        }


        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
        Activity activity = (Activity) getContext();
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(imageView, DetailActivity.IMAGE_TRANSITION_NAME),
                new Pair(roomName, DetailActivity.ROOMNAME_TRANSITION_NAME),
                new Pair(head1, DetailActivity.HEAD1_TRANSITION_NAME),
                new Pair(head2, DetailActivity.HEAD2_TRANSITION_NAME),
                new Pair(head3, DetailActivity.HEAD3_TRANSITION_NAME),
                new Pair(head4, DetailActivity.HEAD4_TRANSITION_NAME)
        );
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(DetailActivity.USER_INFO_IN_ROOM, Parcels.wrap(userInfoInRoom));
        intent.putExtra(DetailActivity.ROOMNAME_TRANSITION_NAME, roomNameInfo);
        intent.putExtra(DetailActivity.ROOMID_TRANSITION_NAME, roomId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void bindData(String imageUrl, List<UserBaseInfo> userInfoInRoom, String roomNameInfo, String roomId) {
        this.imageUrl = imageUrl;
        this.roomNameInfo = roomNameInfo;
        this.userInfoInRoom = userInfoInRoom;
        this.roomId = roomId;
        List<String> avatars = new ArrayList<>();
        for(int i = 0; i < userInfoInRoom.size(); i++) {
            avatars.add(userInfoInRoom.get(i % userInfoInRoom.size()).getAvatarUrl());
        }
        userAvatars = avatars;
        Log.d(TAG, "------->userInfoInRoom.size(): " + userInfoInRoom.size());
//        for(int i = 0; i < avatars.size(); i++) {
//            byte[] imageBytes = Base64.decode(avatars.get(i), Base64.DEFAULT);
//            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            userAvatars.add(bmp);
//        }
    }
}
