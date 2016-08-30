package net.xtion.crm.entity;

import net.xtion.crm.httplibrary.entity.DownloadEntity;

/**
 * Created by Amberllo on 2016/8/30.
 */
public class DocumentDownloadEntity extends DownloadEntity{
    DocumentDownloadEntity(String url, String filename, String dir) {
        super(url, filename, dir);
    }

    @Override
    public void onProgress(int progress) {
        super.onProgress(progress);
    }


}
