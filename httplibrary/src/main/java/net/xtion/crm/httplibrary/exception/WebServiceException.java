package net.xtion.crm.httplibrary.exception;

/**
 * Created by Amberllo on 2016/6/7.
 */
public class WebServiceException extends Exception {

    @Override
    public String getMessage() {
        return "连接服务器失败，请稍后再试！";
    }
}
