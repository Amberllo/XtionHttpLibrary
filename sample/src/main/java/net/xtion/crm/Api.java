package net.xtion.crm;

/**
 * Created by LYL on 2016/8/25.
 */
public class Api {

    public static final String API_HOST = "https://42.159.86.120:81";
    public static final String FILE_HOST = "https://42.159.86.120:82";
    public static final String PUSH_HOST = "42.159.86.120";
    public static final String PUSH_PORT = "1883";

    //基础路径
    public static final String API_BASE = API_HOST +"/api/";

    //登录服务
    public static final String API_Login = API_BASE+"login/loginbymobile";

}
