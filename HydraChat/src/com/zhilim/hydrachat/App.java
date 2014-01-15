package com.zhilim.hydrachat;

import android.app.Application;
import android.content.Intent;
import com.quickblox.module.videochat.core.service.QBVideoChatService;


public class App extends Application {

    @Override
    public void onCreate() {
    	System.out.println("app starting, starting the QBvideoChatService.class");
        super.onCreate();
        startService(new Intent(this, QBVideoChatService.class));
    }
}
