package com.longlongago.resideMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.openvcall.R;
import com.longlongago.openvcall.ui.CreateActivity;
import com.longlongago.openvcall.ui.JoinActivity;
import com.longlongago.roomManager.model.Room;
import com.special.ResideMenu.ResideMenu;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View parentView;
    private ResideMenu resideMenu;

    private static final String TAG = "HomeFragment";

    List<Room> rooms = new ArrayList<>();

    List<List<UserBaseInfo>> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);
        setUpViews();
        return parentView;
    }

    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();

        parentView.findViewById(R.id.button_join_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(parentActivity, JoinActivity.class);
                startActivity(i);
                parentActivity.finish();
            }
        });

        parentView.findViewById(R.id.button_create_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(parentActivity, CreateActivity.class);
                startActivity(i);
                parentActivity.finish();
            }
        });
    }
}
