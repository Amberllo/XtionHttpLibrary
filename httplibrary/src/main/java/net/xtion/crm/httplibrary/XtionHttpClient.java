package net.xtion.crm.httplibrary;

import rx.Observable;

/**
 * Created by LYL on 2016/8/29.
 */
public class XtionHttpClient {
    public static <T extends ResponseEntity> Observable<T> exec(T entity, Object... params){
       return new ServiceProxy(entity).run(params);
    }
}
