package th.co.shiftright.mobile.wheelions.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class APIRequestResult {

	private int status;
	private String data;
	private boolean isTimeout = false;

	public APIRequestResult(HttpResponse response) {
		try {
			if (response != null) {
				status = response.getStatusLine().getStatusCode();
				HttpEntity httpEntity = response.getEntity();
				data = EntityUtils.toString(httpEntity, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int GetStatus() {
		return status;
	}

	public String GetData() {
		return data;
	}

	public boolean isStatusInRange(int start, int end) {
		return status >= start && status <= end;
	}

	public void setTimeout() {
		isTimeout = true;
	}

	public boolean isTimeout() {
		return isTimeout;
	}

    public boolean authenticateNeeded() {
        return status == 401;
    }

}
