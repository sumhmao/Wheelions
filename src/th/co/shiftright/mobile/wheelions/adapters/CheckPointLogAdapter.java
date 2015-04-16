package th.co.shiftright.mobile.wheelions.adapters;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.custom_controls.CheckPointLogItemView;
import th.co.shiftright.mobile.wheelions.imagemanager.AdapterImageManager;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageSize;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CheckPointLogAdapter extends ArrayAdapter<TaskLogData> {

	private final Context context;
	private final ArrayList<TaskLogData> values;
	private AdapterImageManager imageManager;
	private int size;

	public CheckPointLogAdapter(Context context, ArrayList<TaskLogData> values, int size) {
		super(context, 0, values);
		this.context = context;
		this.values = values;
		this.size = size;
		imageManager = new AdapterImageManager(context, this, ImageSize.Thumb);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckPointLogItemView itemView = null;
		if (convertView == null) {
			itemView = new CheckPointLogItemView(context);
			convertView = itemView;
		} else {
			itemView = (CheckPointLogItemView) convertView;
		}
		final TaskLogData data = values.get(position);
		if (data != null) {
			data.generateStaticMap(context);
			itemView.setData(data);
			Bitmap mapImage = imageManager.getImage(data.getStaticMap(), size);
			itemView.setMapImage(mapImage);
		}
		return convertView;
	}

}
