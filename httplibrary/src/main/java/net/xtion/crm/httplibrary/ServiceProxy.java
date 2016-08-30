package net.xtion.crm.httplibrary;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.xtion.crm.httplibrary.entity.DownloadEntity;
import net.xtion.crm.httplibrary.entity.ResponseEntity;
import net.xtion.crm.httplibrary.exception.ConfigChangeException;
import net.xtion.crm.httplibrary.exception.SessionFailedException;
import net.xtion.crm.httplibrary.util.HttpUtil;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Amberllo on 2016/6/7.
 * 代理执行http请求，处理http异常拦截，对象转换，日志打印
 */
public class ServiceProxy{
    private ResponseEntity request;
    public <T extends ResponseEntity> ServiceProxy(T request){
        this.request = request;
    }

    /**
     * 外部发起请求
     * */
    public <T extends ResponseEntity> Observable<T> run(){
        return run(new Object[]{});
    }

    public <T extends ResponseEntity> Observable<T> run(Object[] params){
        return getStrResponse(params).flatMap(new Func1<String, Observable<T>>() {
            @Override
            public Observable<T> call(String json) {
                return paresResponse(json);
            }
        });
    }

    /**
     * 获取请求数据，拦截http异常
     * */
    private Observable<String> getStrResponse(final Object... params) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {

                    String json = request.requestString(params);
//                    Logger.json(json);

                    subscriber.onNext(json);
                    subscriber.onCompleted();
                } catch (SessionFailedException e) {
                    // session过期了
//                    new SystemLogicHelper().runReLogin();

                } catch (Exception e) {

                    e.printStackTrace();
//                    Logger.e(e," entityName = :%s \n url = %s \n args = %s \n error = %s",request.getClass().getSimpleName(),request.makeUrl(),request.createArgs(params),e.getMessage());
                    subscriber.onError(e);
                }

            }
        });
    }


    /**
     * 解析json,生成ResponseEntity，分析entity中errormsg与errorcode，
     * @param json json转化成对象
     * */
    public <T extends ResponseEntity> Observable<T> paresResponse(final String json) {

        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {

                    Class clazz = request.getClass();
                    T response = (T) new Gson().fromJson(json, clazz);

                    if (TextUtils.isEmpty(response.error_msg) && TextUtils.isEmpty(response.error_code)) {
                        request.onSuccess(response);
                        subscriber.onNext(response);
                    } else {
                        request.onError(response);
                        if (response.error_code.equals("-25003")) {
                            throw new ConfigChangeException();
                        } else {
                            throw new Exception(response.error_msg);
                        }
                    }

                } catch (JsonSyntaxException e) {
                    //解析json出错
                    subscriber.onError(new Exception(json));
                } catch (ConfigChangeException e) {
                    // 服务端修改了配置
//                    new SystemLogicHelper().runConfigChange();

                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);

                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }

    public Observable<DownloadEntity> runDownload(final DownloadEntity entity){
        return Observable.create(new Observable.OnSubscribe<DownloadEntity>() {
            @Override
            public void call(final Subscriber<? super DownloadEntity> subscriber) {
                entity.execDownload(new HttpUtil.DownloadListener() {
                    @Override
                    public void onProgress(int progress) {
                        subscriber.onNext(entity);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }
}
