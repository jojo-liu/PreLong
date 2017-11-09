package com.longlongago.openvcall;

import android.app.Activity;
import android.app.Application;

import com.longlongago.login.model.UserBaseInfo;
import com.longlongago.openvcall.model.CurrentUserSettings;
import com.longlongago.openvcall.model.WorkerThread;

import java.util.LinkedList;
import java.util.List;

public class LLAApplication extends Application {

    //对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList实现了基于动态数组的数据结构，要移动数据。LinkedList基于链表的数据结构,便于增加删除
    private List<Activity> activityList = new LinkedList<Activity>();

    private static LLAApplication instance;

    private WorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    public static final CurrentUserSettings mVideoSettings = new CurrentUserSettings();


    //for login and register

    private UserBaseInfo baseUser;//用户基本信息
    // 渠道号
    private String fid = "";
    // 版本号
    private String version = "1.0.0";

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);

    }

    public static void setInstance(LLAApplication instance) {
        LLAApplication.instance = instance;
    }

    /**
     * 获取时间戳
     * @return
     */
    public String getTime(){
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获取ItLanBaoApplication实例
     *
     * @return
     */
    public static LLAApplication getInstance()
    {
        return instance;
    }

    /**
     * 设置用户基本信息
     * @param baseUser
     */
    public void setBaseUser(UserBaseInfo baseUser) {
        this.baseUser = baseUser;
    }


    //添加Activity到容器中
    public void addActivity(Activity activity)  {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit(){
        for(Activity activity:activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
