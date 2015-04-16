package th.co.shiftright.mobile.wheelions.adapters;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.imagemanager.AdapterImageManager;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageSize;
import th.co.shiftright.mobile.wheelions.models.ImageItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GalleryAdapter extends ArrayAdapter<ImageItem> {

	private final Context context;
	private ArrayList<ImageItem> values;
	private AdapterImageManager imageManager;
	private int imageSize;

	public GalleryAdapter(Context context, ArrayList<ImageItem> values, int imageSize) {
		super(context, 0, values);
		this.context = context;
		this.values = values;
		int padding = WheelionsApplication.getDPFromPixel(getContext(), 10);
		this.imageSize = imageSize - (padding * 2);
		imageManager = new AdapterImageManager(context, this, ImageSize.Thumb);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imgPhoto = null;
		if (convertView == null) {
			GridView.LayoutParams param = new GridView.LayoutParams(imageSize, imageSize);
			imgPhoto = new ImageView(context);
			imgPhoto.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
			imgPhoto.setScaleType(ScaleType.CENTER_CROP);
			imgPhoto.setLayoutParams(param);
			convertView = imgPhoto;
		} else {
			imgPhoto = (ImageView) convertView;
		}
		final ImageItem data = values.get(position);
		if (data != null) {
			Bitmap photo = imageManager.getImage(data, imageSize);
			imgPhoto.setImageBitmap(photo);
			imgPhoto.setScaleType(ScaleType.CENTER_CROP);
		}
		return convertView;
	}

}
