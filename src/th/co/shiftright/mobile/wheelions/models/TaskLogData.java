package th.co.shiftright.mobile.wheelions.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.imagemanager.AdapterImageManager;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageCategory;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageDownloadEventListener;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageSize;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class TaskLogData implements Parcelable, ILocation {

	private String id;
	private String name;
	private LatLng location;
	private boolean isHasTime = false;
	private Date time;
	private ArrayList<ImageItem> allPhotos;
	private ImageItem staticMap;
	public ImageItem getStaticMap() {
		return staticMap;
	}

	@Override
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
	@Override
	public LatLng getLocation() {
		return location;
	}
	public String getTimeString() {
		return WheelionsApplication.formatShortDateString(time);
	}
	public String getFullTimeString() {
		return WheelionsApplication.formatLongDateString(time);
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

	public void generateStaticMap(Context context) {
		if (staticMap == null) {
			int width = WheelionsApplication.getScreenWidth(context);
			int height = WheelionsApplication.getDPFromPixel(context, 120);
			String url = String.format(Locale.US, "http://maps.google.com/maps/api/staticmap?center=%f,%f&zoom=15&size=%dx%d&markers=color:blue%%7C%f,%f", 
					getLocation().latitude, getLocation().longitude, width, height, getLocation().latitude, getLocation().longitude);
			staticMap = new ImageItem();
			staticMap.setId(getId());
			staticMap.setImageCategory(ImageCategory.StaticMap);
			staticMap.setOriginal(url);
			staticMap.setPreview(url);
			staticMap.setThumb(url);
			staticMap.setUseResourceUrl(false);
		}
	}

	public AsyncTaskQueueItem getImageTask(final ImageDownloadEventListener listener, final int photoSize) {
		if (getFirstPhoto() != null) {
			return AdapterImageManager.getImageDownloadTask(listener, ImageSize.Original, photoSize, getFirstPhoto(), null);
		} else {
			return null;
		}
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
