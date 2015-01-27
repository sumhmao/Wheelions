package th.co.shiftright.mobile.wheelions.models;

import java.util.Iterator;
import java.util.Locale;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.imagemanager.AdapterImageManager;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageCategory;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageDownloadEventListener;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageSize;

public class User implements Parcelable {

	private String id;
	private String drivingNo;
	private String name;
	private String lastName;
	private String email;
	private String mobile;
	private String employeeNo;
	private ImageItem image;

	public String getID() {
		return id;
	}
	public void setID(String id) {
		this.id = id;
	}
	public String getDrivingNo() {
		return drivingNo;
	}
	public void setDrivingNo(String drivingNo) {
		this.drivingNo = drivingNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getFullName() {
		if (WheelionsApplication.ifStringNotNullOrEmpty(name) && WheelionsApplication.ifStringNotNullOrEmpty(lastName)) {
			return String.format(Locale.US, "%s %s", name, lastName);	
		} else if (WheelionsApplication.ifStringNotNullOrEmpty(name)) {
			return name;
		} else if (WheelionsApplication.ifStringNotNullOrEmpty(lastName)) {
			return lastName;
		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	public User(JSONObject object) {
		Iterator<String> dataIter = object.keys();
		while (dataIter.hasNext()) {
			try {
				String key = dataIter.next();
				if (key.equalsIgnoreCase("id")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						Object data = object.get(key);
						if (data instanceof String) {
							this.setID((String) data);
						} else {
							this.setID(String.format("%d", object.getInt(key)));
						}
					}
				} else if (key.equalsIgnoreCase("driver_license_no")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						Object data = object.get(key);
						if (data instanceof String) {
							this.setDrivingNo((String) data);
						} else {
							this.setDrivingNo(String.format("%d", object.getInt(key)));
						}
					}
				} else if (key.equalsIgnoreCase("firstname")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setName(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("lastname")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setLastName(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("avatar")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						image = new ImageItem(object.getJSONObject(key));
						image.setImageCategory(ImageCategory.User);
						if (!WheelionsApplication.ifStringNotNullOrEmpty(image.getId())) {
							image.setId(getID());
						}
					}
				} else if (key.equalsIgnoreCase("email")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setEmail(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("mobile")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setMobile(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("employee_no")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setEmployeeNo(object.getString(key));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String toJSON() {
		try {
			JSONObject json = new JSONObject();
			json.put("id", getID());
			json.put("driver_license_no", getDrivingNo());
			json.put("firstname", getName());
			json.put("lastName", getLastName());
			if (image != null) {
				JSONObject imgObj = image.toJSONObject();
				json.put("avatar", imgObj);
			}
			json.put("email", getEmail());
			json.put("mobile", getMobile());
			json.put("employee_no", getEmployeeNo());
			return json.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public AsyncTaskQueueItem getImageTask(final ImageDownloadEventListener listener, final int photoSize) {
		if (image != null) {
			return AdapterImageManager.getImageDownloadTask(listener, ImageSize.Thumb, photoSize, image, null);
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
		dest.writeString(drivingNo);
		dest.writeString(name);
		dest.writeString(lastName);
		dest.writeString(email);
		dest.writeString(mobile);
		dest.writeString(employeeNo);
		dest.writeParcelable(image, flags);
	}

	public User(Parcel in) {
		this.id = in.readString();
		this.drivingNo = in.readString();
		this.name = in.readString();
		this.lastName = in.readString();
		this.email = in.readString();
		this.mobile = in.readString();
		this.employeeNo = in.readString();
		this.image = in.readParcelable(ImageItem.class.getClassLoader());
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}
		public User[] newArray(int size) {
			return new User[size];
		}
	};

}
