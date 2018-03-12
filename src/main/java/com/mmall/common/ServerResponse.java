package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by zhaoxin on 2018/3/7.
 */
//保证json序列化时 如果是null对象key会消失
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    //使之不在json序列化结果中
    @JsonIgnore
    public boolean isSuccess(){
        return this.status==RespondeCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return this.status;
    }

    public String getMsg(){
        return this.msg;
    }

    public T getData(){
        return this.data;
    }

    public static  <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(RespondeCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(RespondeCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(RespondeCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return  new ServerResponse<T>(RespondeCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(RespondeCode.ERROR.getCode(),RespondeCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(RespondeCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorMessage(int errorcode,String errorMessage){
        return new ServerResponse<T>(errorcode,errorMessage);
    }

}
