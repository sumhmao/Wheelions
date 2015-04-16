package th.co.shiftright.mobile.wheelions.custom_controls;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.adapters.GalleryAdapter;
import th.co.shiftright.mobile.wheelions.models.ImageItem;
import th.co.shiftright.mobile.wheelions.models.TaskLogData;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckPointLogItemView extends LinearLayout {

	private LayoutInflater mInflater;
	private LinearLayout parentView;
	private ImageView imgMap;
	private TextView lblDate;
	private TextView lblDescription;
	private DynamicHeightGridView grvPhotos;
	private ArrayList<ImageItem> allPhotos;
	private GalleryAdapter adapter;
	private TaskLogData currentData;

	public CheckPointLogItemView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CheckPointLogItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CheckPointLogItemView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		allPhotos = new ArrayList<ImageItem>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = (LinearLayout) mInflater.inflate(R.layout.check_point_log_item_view, this, true);
		imgMap = (ImageView) parentView.findViewById(R.id.imgMap);
		lblDate = (TextView) parentView.findViewById(R.id.lblDate);
		lblDescription = (TextView) parentView.findViewById(R.id.lblDescription);
		grvPhotos = (DynamicHeightGridView) parentView.findViewById(R.id.grvPhotos);
		int imageSize = (WheelionsApplication.getScreenWidth(context) - (WheelionsApplication.getDPFromPixel(context, 22))) / 5;
		adapter = new GalleryAdapter(context, allPhotos, imageSize);
		grvPhotos.setAdapter(adapter);
	}

	public void setData(TaskLogData data) {
		this.currentData = data;
		allPhotos.clear();
		lblDate.setText(currentData.getFullTimeString());
		lblDescription.setText(currentData.getName());
		if (currentData.getAllPhotos().size() > 0) {
			allPhotos.addAll(currentData.getAllPhotos());
			adapter.notifyDataSetChanged();
			grvPhotos.setVisibility(View.VISIBLE);
		} else {
			adapter.notifyDataSetChanged();
			grvPhotos.setVisibility(View.GONE);
		}
	}

	public void setMapImage(Bitmap image) {
		imgMap.setImageBitmap(image);
	}

}
