package net.xtion.crm.httplibrary;

import net.xtion.crm.httplibrary.entity.DownloadEntity;
import net.xtion.crm.httplibrary.entity.ResponseEntity;
import net.xtion.crm.httplibrary.entity.UploadEntity;

import rx.Observable;

/**
 * Created by LYL on 2016/8/29.
 */
public class XtionHttpClient {
    /**
     * 执行http请求 get or post，请求实体需要继承ResponseEntity
     * @param entity 请求实体
     * @param params 请求可边长参数
     * */
    public static <T extends ResponseEntity> Observable<T> exec(T entity, Object... params){
       return new ServiceProxy(entity).run(params);
    }

    /**
     * 执行http download操作，请求实体需要继承DownloadEntity,
     * 在onNext, onComplete提供相应callback
     * @param entity 请求实体
     * */
    public static <T extends DownloadEntity> Observable<T> execDownload(T entity){
        return null;
    }

    /**
     * 执行http upload，请求实体需要继承UploadEntity,在 onNext, onComplete提供相应callback
     * @param entity 请求实体
     * */
    public static <T extends UploadEntity> Observable<T> execUpload(T entity){
        return null;
    }

}
