package net.xtion.crm.httplibrary.entity;

import net.xtion.crm.httplibrary.util.HttpUtil;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntity<T extends ResponseEntity> {
	public String request_id;
	public String error_code;
	public String error_msg;

	protected Method method = Method.Post;

	protected String createArgs(Object[] params) throws JSONException {
		return "";
	}

	protected String makeUrl(){
		return "";
	}

	public String requestString(Object[] params) throws Exception {
		String json;
		String url = makeUrl();

		//只捕抓http内部exception
		if(method== Method.Get){
			json = HttpUtil.execGet(url, header());
		}else{
			String args = createArgs(params);
			json = HttpUtil.execPost(url, args, header());
		}
		return json;
	}

    /** 解析返回值，内部消化结果 */
	public void  onSuccess(T response) throws Exception {

	}

	public void onError(T response) throws Exception {}

	protected Map<String,String> header(){
		return new HashMap<String,String>();
	}

	enum Method{
		Get,Post
	}
}
