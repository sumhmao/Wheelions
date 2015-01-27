package th.co.shiftright.mobile.wheelions.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskStatus implements Parcelable {

	private String code;
	private String title;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public TaskStatus(String code, String title) {
		this.code = code;
		this.title = title;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(title);
	}

	public TaskStatus(Parcel in) {
		this.code = in.readString();
		this.title = in.readString();
	}

	public static final Parcelable.Creator<TaskStatus> CREATOR = new Parcelable.Creator<TaskStatus>() {
		public TaskStatus createFromParcel(Parcel in) {
			return new TaskStatus(in);
		}
		public TaskStatus[] newArray(int size) {
			return new TaskStatus[size];
		}
	};

}
