package th.co.shiftright.mobile.wheelions.models;

import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {

	private String id;
	private String name;

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

	public Company(int index) {
		this.id = String.format(Locale.US, "company%d", index);
		this.name = String.format(Locale.US, "company %d", index);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}

	public Company(Parcel in) {
		this.id = in.readString();
		this.name = in.readString();
	}

	public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
		public Company createFromParcel(Parcel in)
		{
			return new Company(in);
		}
		public Company[] newArray(int size)
		{
			return new Company[size];
		}
	};

}
