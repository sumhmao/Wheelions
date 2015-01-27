package th.co.shiftright.mobile.wheelions.custom_controls;

import java.util.Locale;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskLogListItemView extends RelativeLayout {

	private LayoutInflater mInflater;
	private RelativeLayout parentView;
	private TextView lblLogDescription;
	private ImageView imgThumb;

	public TaskLogListItemView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public TaskLogListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TaskLogListItemView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = (RelativeLayout) mInflater.inflate(R.layout.task_log_list_item_view, this, true);
		lblLogDescription = (TextView) parentView.findViewById(R.id.lblLogDescription);
		imgThumb = (ImageView) parentView.findViewById(R.id.imgThumb);
	}

	public void setData(TaskLogData data, int index) {
		imgThumb.setVisibility(View.GONE);
		if (data != null) {
			String logName = String.format(Locale.US, "%s - %s", data.getTimeString(), data.getName());
			lblLogDescription.setText(logName);
			if (index % 2 == 0) {
				parentView.setBackgroundColor(getResources().getColor(R.color.white));
			} else {
				parentView.setBackgroundColor(getResources().getColor(R.color.light_gray));
			}
		}
	}

	public void setThumbImage(Bitmap photo) {
		if (photo != null) {
			imgThumb.setVisibility(View.VISIBLE);
			imgThumb.setImageBitmap(photo);
		}
	}

}
