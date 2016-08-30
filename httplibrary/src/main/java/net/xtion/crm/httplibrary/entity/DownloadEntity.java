package net.xtion.crm.httplibrary.entity;

import net.xtion.crm.httplibrary.util.HttpUtil;

/**
 * Created by Amberllo on 2016/8/30.
 */
public class DownloadEntity implements HttpUtil.DownloadListener{
    public String filename;
    public String dir;
    public String url;
    private int mProgress;

    public DownloadEntity(String url, String filename, String dir){
        this.url = url;
        this.filename = filename;
        this.dir = dir;
    }

    HttpUtil.DownloadListener listener;

    public void execDownload(HttpUtil.DownloadListener listener){
        this.listener = listener;
        setProgress(0);
        HttpUtil.execDownload(url,  dir,  filename,this);
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
    }

    @Override
    public void onProgress(int progress) {
        setProgress(progress);
        if(listener!=null)listener.onProgress(mProgress);
    }

    @Override
    public void onComplete() {
        setProgress(100);
        if(listener!=null)listener.onComplete();
    }

    @Override
    public void onError(Exception e) {
        if(listener!=null)listener.onError(e);
    }
}
