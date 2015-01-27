package th.co.shiftright.mobile.wheelions.api;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.BasedActivity;
import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.custom_controls.CustomProgressDialog;
import th.co.shiftright.mobile.wheelions.models.WheelionsData;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class APIAsyncBackendCall extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    private APIRequestParam requestParam = null;
    private HttpPost postRequest = null;
    private HttpGet getRequest = null;
    private HttpDelete deleteRequest = null;
    private APIRequestResult result = null;

    public APIAsyncBackendCall(APIRequestParam param) {
        this.activity = param.getAsyncParam().getActivity();
        this.requestParam = param;
    }

    @Override
    protected void onPreExecute() {
        if (!isOnline()) {
            Toast.makeText(activity, "No internet connection", Toast.LENGTH_LONG).show();
            this.cancel(true);
            if (requestParam.getAsyncParam().getListener() != null) {
                requestParam.getAsyncParam().getListener().onNoInternetOrLocation(activity);
            }
        } else if (requestParam.getAsyncParam().getProgressDialog() != null) {
            if (requestParam.getAsyncParam().getProgressDialog() instanceof CustomProgressDialog) {
                CustomProgressDialog customDialog = (CustomProgressDialog) requestParam.getAsyncParam().getProgressDialog();
                customDialog.setAsyncBackendCall(this);
                customDialog.onLocalizationChanged();
            }
            requestParam.getAsyncParam().getProgressDialog().show();
        }
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            HttpResponse response = null;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, WheelionsApplication.CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, WheelionsApplication.SOCKET_TIMEOUT);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            if (requestParam.getMethod() == HttpMethod.POST) {
                postRequest = new HttpPost(requestParam.getUrlString());
                addRequestHeader(postRequest);
                if (requestParam.getSendData() != null) {
                    postRequest.addHeader("Content-Type", "application/json");
                    postRequest.setEntity(new ByteArrayEntity(requestParam.getSendData().getBytes("UTF8")));
                } else if (requestParam.getSendFile() != null) {
                    if (requestParam.getAsyncParam().isMultipart()) {
                        MultipartEntity multipartEntity = new MultipartEntity();
                        for (String fileKey : requestParam.getSendFile().keySet()) {
                            if (requestParam.getSendFile().get(fileKey) instanceof String) {
                                multipartEntity.addPart(fileKey, new StringBody((String) requestParam.getSendFile().get(fileKey), Charset.forName("UTF-8")));
                            } else if (requestParam.getSendFile().get(fileKey) instanceof byte[]) {
                                multipartEntity.addPart(fileKey, new ByteArrayBody((byte[]) requestParam.getSendFile().get(fileKey), fileKey));
                            } else if (requestParam.getSendFile().get(fileKey) instanceof BitmapKeyValue) {
                                BitmapKeyValue val = (BitmapKeyValue) requestParam.getSendFile().get(fileKey);
                                multipartEntity.addPart(fileKey, new ByteArrayBody(val.getBitmapData(), val.getFileName()));
                            } else if (requestParam.getSendFile().get(fileKey) instanceof JSONObject) {
                                StringBody body = new StringBody(requestParam.getSendFile().get(fileKey).toString(), "application/json", Charset.forName("UTF-8"));
                                multipartEntity.addPart(fileKey, body);
                            } else {
                                multipartEntity.addPart(fileKey, new StringBody(requestParam.getSendFile().get(fileKey).toString()));
                            }
                        }
                        postRequest.setEntity(multipartEntity);
                    } else {
                        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                        for (String fileKey : requestParam.getSendFile().keySet()) {
                            if (requestParam.getSendFile().get(fileKey) instanceof String) {
                                parameters.add(new BasicNameValuePair(fileKey, (String) requestParam.getSendFile().get(fileKey)));
                            } else {
                                parameters.add(new BasicNameValuePair(fileKey, requestParam.getSendFile().get(fileKey).toString()));
                            }
                        }
                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                        postRequest.setEntity(ent);
                    }
                }
                response = httpclient.execute(postRequest);
            } else if (requestParam.getMethod() == HttpMethod.GET) {
                getRequest = new HttpGet(requestParam.getUrlString());
                addRequestHeader(getRequest);
                response = httpclient.execute(getRequest);
            } else {
                deleteRequest = new HttpDelete(requestParam.getUrlString());
                addRequestHeader(deleteRequest);
                response = httpclient.execute(deleteRequest);
            }
            result = new APIRequestResult(response);
            if (requestParam.getAsyncParam().getListener() != null) {
                requestParam.getAsyncParam().getListener().doInBackground(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (result == null) {
                result = new APIRequestResult(null);
            }
            result.setTimeout();
        }
        return null;
    }

    private void addRequestHeader(HttpRequestBase request) {
        HashMap<String, String> headers = requestParam.getRequestHeader();
        for (String key : headers.keySet()) {
            request.addHeader(key, headers.get(key));
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Void result) {
        try {
            if (requestParam.getAsyncParam().getProgressDialog() != null) {
                requestParam.getAsyncParam().getProgressDialog().dismiss();
            }
            if (this.result.authenticateNeeded()) {
                Activity activity = requestParam.getAsyncParam().getActivity();
                if (activity != null) {
                    if (activity instanceof BasedActivity) {
                        ((BasedActivity) activity).doLogout();
                    } else {
                        WheelionsData.instance(requestParam.getAsyncParam().getActivity()).logout();
                    }
                }
            } else if (this.result.isTimeout()) {
                if (requestParam.getAsyncParam().getListener() != null) {
                    requestParam.getAsyncParam().getListener().onConnectionTimeout(activity);
                }
            } else if (requestParam.getAsyncParam().getListener() != null) {
                requestParam.getAsyncParam().getListener().onRequestFinish(this.result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }

    public void cancelTask() {
        cancel(true);
        if (postRequest != null) {
            postRequest.abort();
            postRequest = null;
        }
        if (getRequest != null) {
            getRequest.abort();
            getRequest = null;
        }
        if (deleteRequest != null) {
            deleteRequest.abort();
            deleteRequest = null;
        }
    }

    @Override
    protected void onCancelled() {
        CancelRequest();
        super.onCancelled();
    }

    private void CancelRequest() {
        if (requestParam.getAsyncParam().getProgressDialog() != null) {
            requestParam.getAsyncParam().getProgressDialog().dismiss();
        }
        if (requestParam.getAsyncParam().getListener() != null) {
            requestParam.getAsyncParam().getListener().onConnectionTimeout(activity);
        }
    }

}
