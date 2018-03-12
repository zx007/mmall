package com.mmall.common;

/**
 * Created by zhaoxin on 2018/3/7.
 */
public enum  RespondeCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    IEELGAL_ARGUMENT(2,"IEELGAL_ARGUMENT");

    private final int code;
    private final String desc;

    RespondeCode(int code,String desc){
        this.code=code;
        this.desc=desc;
    }

    public int getCode(){
        return this.code;
    }

    public String getDesc(){
        return this.desc;
    }

}
