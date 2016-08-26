package net.xtion.crm.httplibrary.client;

import android.text.TextUtils;
import android.util.Log;


import net.xtion.crm.httplibrary.exception.SessionFailedException;
import net.xtion.crm.httplibrary.exception.WebServiceException;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

public class HttpUtil{

    public static String execPost(String url, String args, Map<String,String> header) throws Exception {

        url = appendSecurity(url);

        HttpClient hClient = null;
        try {
            hClient = getHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(args, "UTF-8"));
        initHeader(httpPost,header);
        HttpResponse httpResponse = hClient.execute(httpPost);

        return execute(httpResponse);
    }

    public static String execGet(String url, Map<String,String> header) throws Exception {

        url = appendSecurity(url);

        String requestid = UUID.randomUUID().toString();
        HttpClient hClient = null;
        try {
            hClient = getHttpClient();
        } catch (Exception e) {
            //getHttpClient的Exception不用抛
            e.printStackTrace();
            return null;
        }

        HttpGet httpGet = new HttpGet(url);
        initHeader(httpGet, header);
        HttpResponse httpResponse = hClient.execute(httpGet);
        return execute(httpResponse);
    }

    private static String execute(HttpResponse httpResponse) throws Exception{
        if (null != httpResponse && null != httpResponse.getStatusLine()) {
            String responentity = parseResponse(httpResponse);
            if (httpResponse.getStatusLine().getStatusCode() >= 200 && httpResponse.getStatusLine().getStatusCode() < 400) {// 200-300为成功
                return responentity;
            } else if (httpResponse.getStatusLine().getStatusCode() == 401) {

                // session过期了
                throw new SessionFailedException();
            } else if (httpResponse.getStatusLine().getStatusCode() >= 500){
                // 服务器错误
                throw new WebServiceException();

            }else {
                return responentity;
            }
        }
        return null;
    }


    private static synchronized HttpClient getHttpClient() throws NoSuchAlgorithmException,
            CertificateException, IOException, KeyStoreException, KeyManagementException,
            UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        SSLSocketFactory sf = new MySSLSocketFactory(keyStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(params, true);

        ConnManagerParams.setTimeout(params, 30000);
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);

        SchemeRegistry schreg = new SchemeRegistry();
        schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schreg.register(new Scheme("https", sf, 443));

        ClientConnectionManager conman = new ThreadSafeClientConnManager(params, schreg);
        HttpClient client = new DefaultHttpClient(conman, params);
        return client;

    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new javax.net.ssl.X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[] {};
                }

            };
            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {

            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }
    }

    private static HostnameVerifier hv = new HostnameVerifier() {

        @Override
        public boolean verify(String urlHostName,SSLSession session) {
            return true;
        }
    };

    private static void trustAllHttpsCertificates() throws Exception {

        // Create a trust manager that does not validate certificate chains:

        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    }

    private static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

    private static String parseResponse(HttpResponse httpResponse) throws IllegalStateException, IOException{
        boolean isGzip = false;
        Header header = httpResponse.getFirstHeader("Content-Encoding");
        if(header!=null && header.getValue().equals("gzip")){
            isGzip = true;
            Log.i("gzip", "true");
        }else{
            Log.i("gzip", "false");
        }

        String responentity = "";
        if(isGzip){
            ByteArrayBuffer bt = new ByteArrayBuffer(4096);
            GZIPInputStream gis = new GZIPInputStream(httpResponse.getEntity().getContent());
            int l;
            byte[] tmp = new byte[4096];
            while ((l = gis.read(tmp)) != -1) {
                bt.append(tmp, 0, l);
            }
            responentity = new String(bt.toByteArray(), "utf-8");
        }else{
            responentity = EntityUtils.toString(httpResponse.getEntity());
        }
        return responentity;
    }

    private static String appendSecurity(String url) throws MalformedURLException{

        String api = new URL(url).getPath();
        long ts = System.currentTimeMillis()/1000;
        int ex = 300;
        String si = MD5Util.stringToMD5(api.substring(1)+Constants.ApiSecretKey);
        return url+String.format("?ts=%d&ex=%d&si=%s", new Object[]{ts,ex,si});
    }

    private static void initHeader(HttpMessage httpmessage,Map<String,String> header){
        Iterator entries = header.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            httpmessage.addHeader(key,value);
        }

        entries = defaultHeader.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            httpmessage.addHeader(key,value);
        }

    }

    private static Map<String,String> defaultHeader = null;

    static{
        defaultHeader = new HashMap<>();
        defaultHeader.put("device","Android");
        defaultHeader.put("Content-Type", "application/json");
        defaultHeader.put("Accept", "application/json");
        defaultHeader.put("appid", Constants.APPID);
        defaultHeader.put("Accept-Encoding","gzip, deflate");
        defaultHeader.put("s", "m");
        defaultHeader.put("v", "v2");
    }

    public static Map<String,String> createHeader(String deviceid,String usernumber,String sessionid,String enterprisenumber){
        Map<String,String> result = new HashMap<String,String>();
        result.put("dv",deviceid);
        result.put("sig", TextUtils.isEmpty(sessionid)?"":sessionid);
        result.put("userno", usernumber);
        result.put("e", enterprisenumber);
        result.put("reqid", UUID.randomUUID().toString());
        return result;
    }
}
