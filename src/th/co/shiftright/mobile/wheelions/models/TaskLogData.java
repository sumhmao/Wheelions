package th.co.shiftright.mobile.wheelions.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageCategory;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskLogData implements Parcelable {

	private String id;
	private String name;
	private LatLng location;
	private boolean isHasTime = false;
	private Date time;
	private ArrayList<ImageItem> allPhotos;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public LatLng getLocation() {
		return location;
	}
	public String getTimeString() {
		return WheelionsApplication.formatShortDateString(time);
	}
	public ArrayList<ImageItem> getAllPhotos() {
		return allPhotos;
	}
	public ImageItem getFirstPhoto() {
		if (allPhotos.size() > 0) {
			return allPhotos.get(0);
		} else {
			return null;
		}
	}

	private void init() {
		allPhotos = new ArrayList<ImageItem>();
	}

	@SuppressWarnings("unchecked")
	public TaskLogData(JSONObject object) {
		init();
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
				} else if (key.equalsIgnoreCase("note")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setName(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("latitude")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						latitude = object.getDouble(key);
					}
				} else if (key.equalsIgnoreCase("longitude")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						longitude = object.getDouble(key);
					}
				} else if (key.equalsIgnoreCase("created_at")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						if (object.getString(key) instanceof String) {
							String timeString = object.getString(key);
							this.setTime(WheelionsApplication.getDateFromString(timeString));
							isHasTime = true;
						}
					}
				} else if (key.equalsIgnoreCase("images")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						JSONArray array = object.getJSONArray(key);
						for (int i = 0; i < array.length(); i++) {
							ImageItem logPhoto = new ImageItem(array.getJSONObject(i));
							logPhoto.setImageCategory(ImageCategory.TaskLog);
							allPhotos.add(logPhoto);
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
		dest.writeString(name);
		dest.writeParcelable(location, flags);
		dest.writeTypedList(allPhotos);
		dest.writeValue(Boolean.valueOf(isHasTime));
		if (isHasTime) {
			dest.writeLong(getTime().getTime());
		}
	}

	public TaskLogData(Parcel in) {
		init();
		this.id = in.readString();
		this.name = in.readString();
		this.location = in.readParcelable(LatLng.class.getClassLoader());
		in.readTypedList(allPhotos, ImageItem.CREATOR);
		this.isHasTime = ((Boolean) in.readValue(Boolean.class.getClassLoader())).booleanValue();
		if (isHasTime) {
			this.time = new Date(in.readLong());
		}
	}

	public static final Parcelable.Creator<TaskLogData> CREATOR = new Parcelable.Creator<TaskLogData>() {
		public TaskLogData createFromParcel(Parcel in)
		{
			return new TaskLogData(in);
		}
		public TaskLogData[] newArray(int size)
		{
			return new TaskLogData[size];
		}
	};

}
