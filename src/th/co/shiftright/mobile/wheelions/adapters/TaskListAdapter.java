package th.co.shiftright.mobile.wheelions.adapters;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.custom_controls.TaskListItemView;
import th.co.shiftright.mobile.wheelions.models.TaskData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TaskListAdapter extends ArrayAdapter<TaskData> {

	private final Context context;
	private final ArrayList<TaskData> values;

	public TaskListAdapter(Context context, ArrayList<TaskData> values) {
		super(context, 0, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TaskListItemView itemView = null;
		if (convertView == null) {
			itemView = new TaskListItemView(context);
			convertView = itemView;
		} else {
			itemView = (TaskListItemView) convertView;
		}
		final TaskData data = values.get(position);
		if (data != null) {
			itemView.setData(data, position);
		}
		return convertView;
	}

}
