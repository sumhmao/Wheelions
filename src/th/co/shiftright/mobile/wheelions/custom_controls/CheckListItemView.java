package th.co.shiftright.mobile.wheelions.custom_controls;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.models.CheckPoint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckListItemView extends LinearLayout {

	private LayoutInflater mInflater;
	private LinearLayout parentView;
	private ImageView imgCheckIcon;
	private View line;
	private TextView lblListTitle;
	private ImageView imgPhotoNeeded;
	private CheckPoint currentData;
	public CheckPoint getData() {
		return currentData;
	}

	public CheckListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CheckListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CheckListItemView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = (LinearLayout) mInflater.inflate(R.layout.check_list_item_view, this, true);
		imgCheckIcon = (ImageView) parentView.findViewById(R.id.imgCheckIcon);
		line = parentView.findViewById(R.id.line);
		lblListTitle = (TextView) parentView.findViewById(R.id.lblListTitle);
		imgPhotoNeeded = (ImageView) parentView.findViewById(R.id.imgPhotoNeeded);
	}

	public void setData(CheckPoint data, boolean showLine) {
		currentData = data;
		lblListTitle.setText(data.getTitle());
		imgCheckIcon.setSelected(data.isFinished());
		if (showLine) {
			line.setVisibility(View.VISIBLE);
		} else {
			line.setVisibility(View.INVISIBLE);
		}
		if (data.isRequiredPhoto()) {
			imgPhotoNeeded.setVisibility(View.VISIBLE);
		} else {
			imgPhotoNeeded.setVisibility(View.GONE);
		}
	}

}
