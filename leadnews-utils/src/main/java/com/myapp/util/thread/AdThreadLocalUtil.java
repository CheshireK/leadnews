package com.myapp.util.thread;

import com.myapp.model.admin.pojo.AdUser;

public class AdThreadLocalUtil {
    private final static ThreadLocal<AdUser> AD_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(AdUser user){
        AD_USER_THREAD_LOCAL.set(user);
    }

    public static AdUser getUser(){
        return AD_USER_THREAD_LOCAL.get();
    }

    public static void clear(){
        AD_USER_THREAD_LOCAL.remove();
    }
}
