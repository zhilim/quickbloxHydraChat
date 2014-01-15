package com.zhilim.hydrachat.models;

import com.quickblox.module.users.model.QBUser;

public class DataHolder {

    private static DataHolder dataHolder;
    private QBUser currentQbUser;

    private DataHolder() {
    }

    public static synchronized DataHolder getInstance() {
        if (dataHolder == null) {
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }

    public void setCurrentQbUser(int currentQbUserId, String password) {
        this.currentQbUser = new QBUser(currentQbUserId);
        this.currentQbUser.setPassword(password);
    }

    public QBUser getCurrentQbUser() {
        return currentQbUser;
    }

}
