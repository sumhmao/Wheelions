package th.co.shiftright.mobile.wheelions.models;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskData implements Parcelable {

	private String id;
	private String taskNo;
	private Company company = null;
	private String description;
	private String from;
	private String to;
	private String note;
	private ArrayList<TaskLogData> allLogs;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getCompanyName() {
		if (company != null) {
			return company.getName();
		}
		return "No data";
	}
	public ArrayList<TaskLogData> getAllLogs() {
		return allLogs;
	}

	private void initializeData() {
		allLogs = new ArrayList<TaskLogData>();
	}

	@SuppressWarnings("unchecked")
	public TaskData(JSONObject object) {
		initializeData();
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
				} else if (key.equalsIgnoreCase("title")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setDescription(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("no")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setTaskNo(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("from")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setFrom(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("to")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setTo(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("note")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setNote(object.getString(key));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(description);
		dest.writeString(taskNo);
		dest.writeString(from);
		dest.writeString(to);
		dest.writeString(note);
		dest.writeParcelable(company, flags);
		dest.writeTypedList(allLogs);
	}

	public TaskData(Parcel in) {
		initializeData();
		this.id = in.readString();
		this.description = in.readString();
		this.taskNo = in.readString();
		this.from = in.readString();
		this.to = in.readString();
		this.note = in.readString();
		this.company = in.readParcelable(Company.class.getClassLoader());
		in.readTypedList(allLogs, TaskLogData.CREATOR);
	}

	public static final Parcelable.Creator<TaskData> CREATOR = new Parcelable.Creator<TaskData>() {
		public TaskData createFromParcel(Parcel in)
		{
			return new TaskData(in);
		}
		public TaskData[] newArray(int size)
		{
			return new TaskData[size];
		}
	};

}
