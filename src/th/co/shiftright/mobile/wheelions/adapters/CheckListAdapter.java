package th.co.shiftright.mobile.wheelions.adapters;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.custom_controls.CheckListItemView;
import th.co.shiftright.mobile.wheelions.models.CheckPoint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CheckListAdapter extends ArrayAdapter<CheckPoint> {

	private final Context context;
	private final ArrayList<CheckPoint> values;

	public CheckListAdapter(Context context, ArrayList<CheckPoint> values) {
		super(context, 0, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckListItemView itemView = null;
		if (convertView == null) {
			itemView = new CheckListItemView(context);
			convertView = itemView;
		} else {
			itemView = (CheckListItemView) convertView;
		}
		final CheckPoint data = values.get(position);
		boolean showLine = true;
		if (position == values.size() - 1) {
			showLine = false;
		}
		if (data != null) {
			itemView.setData(data, showLine);
		}
		return convertView;
	}

}
