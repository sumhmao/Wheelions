package th.co.shiftright.mobile.wheelions.adapters;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.custom_controls.TaskLogListItemView;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TaskLogAdapter extends ArrayAdapter<TaskLogData> {

	private final Context context;
	private final ArrayList<TaskLogData> values;

	public TaskLogAdapter(Context context, ArrayList<TaskLogData> values) {
		super(context, 0, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TaskLogListItemView itemView = null;
		if (convertView == null) {
			itemView = new TaskLogListItemView(context);
			convertView = itemView;
		} else {
			itemView = (TaskLogListItemView) convertView;
		}
		final TaskLogData data = values.get(position);
		if (data != null) {
			itemView.setData(data, position);
		}
		return convertView;
	}

}
