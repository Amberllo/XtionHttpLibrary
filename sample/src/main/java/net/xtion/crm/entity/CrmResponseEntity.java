package net.xtion.crm.entity;

import net.xtion.crm.cache.MemoryCache;
import net.xtion.crm.httplibrary.util.HttpClientUtil;
import net.xtion.crm.httplibrary.util.HttpUtil;
import net.xtion.crm.httplibrary.entity.ResponseEntity;

import java.util.Map;

/**
 * Created by LYL on 2016/8/29.
 */
public class CrmResponseEntity extends ResponseEntity{

    @Override
    protected Map<String, String> header() {
        String devideid =  MemoryCache.getInstance().get("deviceid");
        String usernumber =  MemoryCache.getInstance().get("usernumber");
        String sessionid = MemoryCache.getInstance().get("sessionid");
        String enterprisenumber = MemoryCache.getInstance().get("enterprisenumber");
        return HttpClientUtil.createHeader(devideid,usernumber,sessionid,enterprisenumber);
    }


}
