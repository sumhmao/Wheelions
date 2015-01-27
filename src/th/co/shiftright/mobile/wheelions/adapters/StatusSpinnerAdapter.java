package th.co.shiftright.mobile.wheelions.adapters;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.models.TaskStatus;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatusSpinnerAdapter extends ArrayAdapter<TaskStatus> {

	private final Context context;
	private ArrayList<TaskStatus> values;

	public StatusSpinnerAdapter(Context context, ArrayList<TaskStatus> values) {
		super(context, 0, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null || convertView.getTag() == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.status_spinner_item, parent, false);
			CafeCategorySpinnerItemHolder holder = new CafeCategorySpinnerItemHolder();
			holder.statusName = (TextView) convertView.findViewById(R.id.lblStatusName);
			holder.statusNameSpinner = (TextView) convertView.findViewById(R.id.lblStatusNameSpinner);
			convertView.setTag(holder);
		}
		final TaskStatus data = values.get(position);
		if (data != null) {
			final CafeCategorySpinnerItemHolder holder = (CafeCategorySpinnerItemHolder) convertView.getTag();
			holder.statusName.setText(data.getTitle());
			holder.statusName.setVisibility(View.VISIBLE);
			holder.statusNameSpinner.setText(data.getTitle());
			holder.statusNameSpinner.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null || convertView.getTag() == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.status_spinner_item, parent, false);
			CafeCategorySpinnerItemHolder holder = new CafeCategorySpinnerItemHolder();
			holder.statusName = (TextView) convertView.findViewById(R.id.lblStatusName);
			holder.statusNameSpinner = (TextView) convertView.findViewById(R.id.lblStatusNameSpinner);
			convertView.setTag(holder);
		}
		final TaskStatus data = values.get(position);
		if (data != null) {
			final CafeCategorySpinnerItemHolder holder = (CafeCategorySpinnerItemHolder) convertView.getTag();
			holder.statusName.setText(data.getTitle());
			holder.statusName.setVisibility(View.GONE);
			holder.statusNameSpinner.setText(data.getTitle());
			holder.statusNameSpinner.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	static class CafeCategorySpinnerItemHolder {
		TextView statusName;
		TextView statusNameSpinner;
	}

}
