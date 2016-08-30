package net.xtion.crm.httplibrary.util;

import net.xtion.crm.httplibrary.exception.FileValidateException;
import net.xtion.crm.httplibrary.exception.SessionFailedException;
import net.xtion.crm.httplibrary.exception.WebServiceException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

public class HttpUtil{

    public static String execPost(String url, String args, Map<String,String> header) throws Exception {

        url = HttpClientUtil.appendSecurity(url);

        HttpClient hClient = null;
        try {
            hClient = HttpClientUtil.getHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(args, "UTF-8"));
        HttpClientUtil.initHeader(httpPost,header);
        HttpResponse httpResponse = hClient.execute(httpPost);

        return execute(httpResponse);
    }

    public static String execGet(String url, Map<String,String> header) throws Exception {

        url = HttpClientUtil.appendSecurity(url);

        HttpClient hClient = null;
        try {
            hClient = HttpClientUtil.getHttpClient();
        } catch (Exception e) {
            //getHttpClient的Exception不用抛
            e.printStackTrace();
            return null;
        }

        HttpGet httpGet = new HttpGet(url);
        HttpClientUtil.initHeader(httpGet, header);
        HttpResponse httpResponse = hClient.execute(httpGet);
        return execute(httpResponse);
    }

    private static String execute(HttpResponse httpResponse) throws Exception{
        if (null != httpResponse && null != httpResponse.getStatusLine()) {
            String responentity = HttpClientUtil.parseResponse(httpResponse);
            int code = httpResponse.getStatusLine().getStatusCode();
            if ( code >= 200 && code < 400) {// 200-300为成功
                return responentity;
            } else if (code == 401) {

                // session过期了
                throw new SessionFailedException();
            } else if (code >= 500){
                // 服务器错误
                throw new WebServiceException();

            }else {
                return responentity;
            }
        }
        return null;
    }


    /**
     * 下载文件
     * */
    public static boolean execDownload(String serviceUrl, String dir, String filename, DownloadListener listener){
        // 下载网络文件
        FileOutputStream fileOutputStream = null;
        InputStream inStream = null;

        try {
            HttpURLConnection conn = HttpClientUtil.getHttpConnection(serviceUrl);

            File file = FileUtil.createFile(dir,filename);
            inStream = conn.getInputStream();
            fileOutputStream = new FileOutputStream(file);

            int code = conn.getResponseCode();
            if (code == 401) {
                // session过期了
                throw new SessionFailedException();
            } else if (code >= 500){
                // 服务器错误
                throw new WebServiceException();
            }
            long total = conn.getContentLength();
            byte[] buf = new byte[1024 * 256];
            int ch = -1;
            int count = 0;
            int preProgress = 0;
            while ((ch = inStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, ch);
                count += ch;
                int progress = total == -1 ? -1: (int) ((count * 100) / total);
                if(progress>preProgress){
                    if(listener!=null)listener.onProgress(progress);
                }
                preProgress = progress;
            }
            fileOutputStream.flush();
            fileOutputStream.close();

            if(total!=-1 ){

                if(count!=total){
                    //下载文件长度校验问题
                    FileUtil.deleteFile(dir+"/"+filename);
                    throw new FileValidateException();
                }
            }
            //下载成功
            if(listener!=null)listener.onComplete();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            FileUtil.deleteFile(dir+"/"+filename);
            if(listener!=null)listener.onError(e);
            return false;
        }
        finally{
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface DownloadListener{
        void onProgress(int progress);
        void onComplete();
        void onError(Exception e);
    }
}
