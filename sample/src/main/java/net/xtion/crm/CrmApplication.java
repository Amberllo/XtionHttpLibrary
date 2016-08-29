package net.xtion.crm;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import net.xtion.crm.cache.MemoryCache;

import java.util.UUID;

/**
 * Created by LYL on 2016/8/29.
 */
public class CrmApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        MemoryCache.getInstance().put("deviceid",getDeviceUUID(this));
    }

    public static String getDeviceUUID(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }
}
