package th.co.shiftright.mobile.wheelions.custom_controls;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImagePreviewItem extends RelativeLayout {

	private LayoutInflater mInflater;
	private RelativeLayout parentView;
	private ImageView imgPhoto;
	private ImageButton btnRemove;
	private ImagePreviewItemListener listener = null;
	public void setItemListener(ImagePreviewItemListener listener) {
		this.listener = listener;
	}
	private Uri currentImage = null;

	public ImagePreviewItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ImagePreviewItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ImagePreviewItem(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = (RelativeLayout) mInflater.inflate(R.layout.image_preview_item, this, true);
		imgPhoto = (ImageView) parentView.findViewById(R.id.imgPhoto);
		btnRemove = (ImageButton) parentView.findViewById(R.id.btnRemove);
		btnRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null && currentImage != null) {
					listener.onImageRemove(currentImage);
				}
			}
		});
	}

	public void setData(Activity activity, Uri imageUri) {
		currentImage = imageUri;
		if (imageUri != null) {
			Bitmap photo = WheelionsApplication.getBitmapFromUri(activity, imageUri);
			imgPhoto.setImageBitmap(photo);
		} else {
			imgPhoto.setImageBitmap(null);
		}
	}

}
