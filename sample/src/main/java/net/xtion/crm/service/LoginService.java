package net.xtion.crm.service;


import net.xtion.crm.entity.LoginByMobileEntity;
import net.xtion.crm.httplibrary.XtionHttpClient;

import rx.Observable;

/**
 * Created by Amberllo on 2016/8/25.
 */
public class LoginService{

    public static Observable<LoginByMobileEntity> loginByMoble(String username, String password, boolean isRemember){
        return XtionHttpClient.exec(new LoginByMobileEntity(),username,password,isRemember);
    }
}
