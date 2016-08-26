package net.xtion.crm.httplibrary;

import android.database.Observable;

import net.xtion.crm.httplibrary.entity.ResponseEntity;

/**
 * Created by Amberllo on 2016/8/26.
 */
public interface XtionHttpClient<T extends ResponseEntity> {
    Observable<T> exec(T request);
}
