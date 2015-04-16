package th.co.shiftright.mobile.wheelions.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class CheckPoint implements Parcelable {

	private String id;
	private String jobId;
	private boolean requiredPhoto;
	private boolean hasPOI;
	private String title;
	private String description;
	private String note;
	private LatLng location;
	private int orderNo;
	private boolean isFinished;
	private boolean isCompleted;
	private Date finishedAt;
	private boolean hasFinished = false;
	private Date createdAt;
	private boolean hasCreated = false;
	private Date updatedAt;
	private boolean hasUpdated = false;
	private ArrayList<TaskLogData> allLogs;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public boolean isRequiredPhoto() {
		return requiredPhoto;
	}
	public void setRequiredPhoto(boolean requiredPhoto) {
		this.requiredPhoto = requiredPhoto;
	}
	public boolean isHasPOI() {
		return hasPOI;
	}
	public void setHasPOI(boolean hasPOI) {
		this.hasPOI = hasPOI;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public int getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
	public boolean isFinished() {
		return isFinished;
	}
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	public boolean isCompleted() {
		return isCompleted;
	}
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	public Date getFinishedAt() {
		return finishedAt;
	}
	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	public ArrayList<TaskLogData> getAllLogs() {
		return allLogs;
	}
	public void setAllLogs(ArrayList<TaskLogData> allLogs) {
		this.allLogs = allLogs;
	}

	private void initializeData() {
		allLogs = new ArrayList<TaskLogData>();
	}

	@SuppressWarnings("unchecked")
	public CheckPoint(JSONObject object) {
		initializeData();
		double latitude = 0;
		double longitude = 0;
		Iterator<String> dataIter = object.keys();
		while (dataIter.hasNext()) {
			try {
				String key = dataIter.next();
				if (key.equalsIgnoreCase("id")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						Object data = object.get(key);
						if (data instanceof String) {
							this.setId((String) data);
						} else {
							this.setId(String.format("%d", object.getInt(key)));
						}
					}
				} else if (key.equalsIgnoreCase("job_id")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						Object data = object.get(key);
						if (data instanceof String) {
							this.setJobId((String) data);
						} else {
							this.setJobId(String.format("%d", object.getInt(key)));
						}
					}
				} else if (key.equalsIgnoreCase("required_photo")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setRequiredPhoto(WheelionsApplication.getBoolValueFromResponse(object.get(key)));
					}
				} else if (key.equalsIgnoreCase("has_poi")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setHasPOI(WheelionsApplication.getBoolValueFromResponse(object.get(key)));
					}
				} else if (key.equalsIgnoreCase("title")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setTitle(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("description")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setDescription(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("note")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setNote(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("latitude")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						latitude = object.getDouble(key);
					}
				} else if (key.equalsIgnoreCase("longitude")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						longitude = object.getDouble(key);
					}
				} else if (key.equalsIgnoreCase("order_no")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setOrderNo(object.getInt(key));
					}
				} else if (key.equalsIgnoreCase("is_finished")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setFinished(WheelionsApplication.getBoolValueFromResponse(object.get(key)));
					}
				} else if (key.equalsIgnoreCase("is_completed")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setCompleted(WheelionsApplication.getBoolValueFromResponse(object.get(key)));
					}
				} else if (key.equalsIgnoreCase("finished_at")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						if (object.getString(key) instanceof String) {
							String timeString = object.getString(key);
							this.setFinishedAt(WheelionsApplication.getDateFromString(timeString));
							hasFinished = true;
						}
					}
				} else if (key.equalsIgnoreCase("created_at")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						if (object.getString(key) instanceof String) {
							String timeString = object.getString(key);
							this.setCreatedAt(WheelionsApplication.getDateFromString(timeString));
							hasCreated = true;
						}
					}
				} else if (key.equalsIgnoreCase("updated_at")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						if (object.getString(key) instanceof String) {
							String timeString = object.getString(key);
							this.setUpdatedAt(WheelionsApplication.getDateFromString(timeString));
							hasUpdated = true;
						}
					}
				} else if (key.equalsIgnoreCase("logs")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						JSONArray logsArray = object.getJSONArray(key);
						for (int i = 0; i < logsArray.length(); i++) {
							JSONObject logObj = logsArray.getJSONObject(i);
							TaskLogData log = new TaskLogData(logObj);
							allLogs.add(log);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		location = new LatLng(latitude, longitude);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(jobId);
		dest.writeValue(Boolean.valueOf(requiredPhoto));
		dest.writeValue(Boolean.valueOf(hasPOI));
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(note);
		dest.writeParcelable(location, flags);
		dest.writeInt(orderNo);
		dest.writeValue(Boolean.valueOf(isFinished));
		dest.writeValue(Boolean.valueOf(isCompleted));
		dest.writeValue(Boolean.valueOf(hasFinished));
		if (hasFinished) {
			dest.writeLong(getFinishedAt().getTime());
		}
		dest.writeValue(Boolean.valueOf(hasCreated));
		if (hasCreated) {
			dest.writeLong(getCreatedAt().getTime());
		}
		dest.writeValue(Boolean.valueOf(hasUpdated));
		if (hasUpdated) {
			dest.writeLong(getUpdatedAt().getTime());
		}
		dest.writeTypedList(allLogs);
	}

	public CheckPoint(Parcel in) {
		initializeData();
		this.id = in.readString();
		this.jobId = in.readString();
		this.requiredPhoto = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		this.hasPOI = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		this.title = in.readString();
		this.description = in.readString();
		this.note = in.readString();
		this.location = in.readParcelable(LatLng.class.getClassLoader());
		this.orderNo = in.readInt();
		this.isFinished = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		this.isCompleted = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		this.hasFinished = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		if (hasFinished) {
			this.finishedAt = new Date(in.readLong());
		}
		this.hasCreated = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		if (hasCreated) {
			this.createdAt = new Date(in.readLong());
		}
		this.hasUpdated = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		if (hasUpdated) {
			this.updatedAt = new Date(in.readLong());
		}
		in.readTypedList(allLogs, TaskLogData.CREATOR);
	}

	public static final Parcelable.Creator<CheckPoint> CREATOR = new Parcelable.Creator<CheckPoint>() {
		public CheckPoint createFromParcel(Parcel in)
		{
			return new CheckPoint(in);
		}
		public CheckPoint[] newArray(int size)
		{
			return new CheckPoint[size];
		}
	};

}
