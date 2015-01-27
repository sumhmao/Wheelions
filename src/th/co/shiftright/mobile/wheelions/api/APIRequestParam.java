package th.co.shiftright.mobile.wheelions.api;

import java.util.HashMap;

public class APIRequestParam {

	private String urlString;
	private HttpMethod method;
	private String sendData;
	private HashMap<String, String> requestHeader;
	private HashMap<String, Object> sendFile;
	private APIAsyncParam asyncParam;

	public APIRequestParam(String urlString, HttpMethod method, 
			APIAsyncParam asyncParam, HashMap<String, String> requestHeader) {
		this.urlString = urlString;
		this.method = method;
		this.asyncParam = asyncParam;
		this.requestHeader = requestHeader;
	}
	
	public APIRequestParam(String urlString, HttpMethod method, String sendData, 
			APIAsyncParam asyncParam, HashMap<String, String> requestHeader) {
		this.urlString = urlString;
		this.method = method;
		this.sendData = sendData;
		this.asyncParam = asyncParam;
		this.requestHeader = requestHeader;
	}

	public APIRequestParam(String urlString, HttpMethod method, 
			HashMap<String, Object> sendFile, APIAsyncParam asyncParam ,HashMap<String, String> requestHeader) {
		this.urlString = urlString;
		this.method = method;
		this.sendFile = sendFile;
		this.asyncParam = asyncParam;
		this.requestHeader = requestHeader;
	}

	public String getUrlString() {
		return urlString;
	}
	public HttpMethod getMethod() {
		return method;
	}
	public String getSendData() {
		return sendData;
	}
	public HashMap<String, Object> getSendFile() {
		return sendFile;
	}
	public APIAsyncParam getAsyncParam() {
		return asyncParam;
	}
	public HashMap<String, String> getRequestHeader() {
		return requestHeader;
	}

}
