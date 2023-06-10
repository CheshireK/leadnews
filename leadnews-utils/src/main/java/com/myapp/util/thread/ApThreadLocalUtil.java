package com.myapp.util.thread;

import com.myapp.model.user.pojo.ApUser;

public class ApThreadLocalUtil {
    private final static ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(ApUser user){
        AP_USER_THREAD_LOCAL.set(user);
    }

    public static ApUser getUser(){
        return AP_USER_THREAD_LOCAL.get();
    }

    public static void clear(){
        AP_USER_THREAD_LOCAL.remove();
    }
}
