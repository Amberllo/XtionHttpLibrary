package net.xtion.crm.entity;

import net.xtion.crm.Api;
import net.xtion.crm.httplibrary.ResponseEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LYL on 2016/8/29.
 */
public class LoginByMobileEntity extends CrmResponseEntity{
    /**
     * accountno : 1511214430030
     * usernumber : 644300
     * sessionid : cf277c7f-abd1-4c7b-8c0f-3c566456aeec
     * servertime : 1472453540367
     * enterprisenumber : 1007807
     */

    public ResponseParams response_params;

    @Override
    protected String makeUrl() {
        return Api.API_Login;
    }

    @Override
    public void onSuccess(ResponseEntity response) throws Exception {
        System.out.println(response);
    }

    @Override
    protected String createArgs(Object[] params) throws JSONException{
        JSONObject job = new JSONObject();
        JSONObject accountobj = new JSONObject();
        accountobj.put("usernumber",params[0]);
        accountobj.put("password",params[1]);
        job.put("accountobj",accountobj);
        return job.toString();
    }

    @Override
    protected String requestString(Object[] params) throws Exception {
        return super.requestString(params);
    }

    public class ResponseParams {
        public String accountno;
        public int usernumber;
        public String sessionid;
        public String servertime;
        public int enterprisenumber;

    }
}
