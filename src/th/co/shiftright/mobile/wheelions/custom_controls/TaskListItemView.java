package th.co.shiftright.mobile.wheelions.custom_controls;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.models.TaskData;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskListItemView extends RelativeLayout {

	private LayoutInflater mInflater;
	private RelativeLayout parentView;
	private TextView lblTaskNo;
	private TextView lblTaskDescription;

	public TaskListItemView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public TaskListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TaskListItemView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = (RelativeLayout) mInflater.inflate(R.layout.task_list_item_view, this, true);
		lblTaskNo = (TextView) parentView.findViewById(R.id.lblTaskNo);
		lblTaskDescription = (TextView) parentView.findViewById(R.id.lblTaskDescription);
	}

	public void setData(TaskData data, int index) {
		if (data != null) {
			lblTaskNo.setText(data.getTaskNo());
			lblTaskDescription.setText(data.getDescription());
			if (index % 2 == 0) {
				parentView.setBackgroundColor(getResources().getColor(R.color.white));
			} else {
				parentView.setBackgroundColor(getResources().getColor(R.color.light_gray));
			}
		}
	}

}
