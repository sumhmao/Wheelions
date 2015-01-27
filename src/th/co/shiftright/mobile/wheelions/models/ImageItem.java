package th.co.shiftright.mobile.wheelions.models;

import java.util.Iterator;

import org.json.JSONObject;

import th.co.shiftright.mobile.wheelions.WheelionsApplication;
import th.co.shiftright.mobile.wheelions.imagemanager.IImageItem;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageCategory;
import th.co.shiftright.mobile.wheelions.imagemanager.ImageSize;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Parcelable, IImageItem {

	private String id;
	private String thumb;
	private String preview;
	private String original;
	private ImageCategory imageCategory = ImageCategory.User;
	public void setImageCategory(ImageCategory category) {
		imageCategory = category;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getPreview() {
		return preview;
	}
	public void setPreview(String preview) {
		this.preview = preview;
	}
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}

	public ImageItem() {}

	@SuppressWarnings("unchecked")
	public ImageItem(JSONObject object) {
		Iterator<String> dataIter = object.keys();
		while (dataIter.hasNext()) {
			try {
				String key = dataIter.next();
				if (key.equalsIgnoreCase("id")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						Object data = object.get(key);
						if (data instanceof String) {
							this.setId((String) data);
						} else {
							this.setId(String.format("%d", object.getInt(key)));
						}
					}
				} else if (key.equalsIgnoreCase("thumb")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setThumb(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("preview")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setPreview(object.getString(key));
					}
				} else if (key.equalsIgnoreCase("original")) {
					if (WheelionsApplication.checkJSONObjectForKey(key, object)) {
						this.setOriginal(object.getString(key));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public JSONObject toJSONObject() {
		try {
			JSONObject json = new JSONObject();
			json.put("id", getId());
			json.put("thumb", getThumb());
			json.put("preview", getPreview());
			json.put("original", getOriginal());
			return json;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(thumb);
		dest.writeString(preview);
		dest.writeString(original);
		dest.writeString(ImageCategory.toString(imageCategory));
	}

	public ImageItem(Parcel in) {
		this.id = in.readString();
		this.thumb = in.readString();
		this.preview = in.readString();
		this.original = in.readString();
		String category = in.readString();
		this.imageCategory = ImageCategory.fromString(category);
	}

	public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
		public ImageItem createFromParcel(Parcel in) {
			return new ImageItem(in);
		}
		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
	};

	@Override
	public ImageCategory getImageCategory() {
		return imageCategory;
	}
	@Override
	public String getImageUrl(ImageSize size) {
		switch (size) {
		case Thumb:
			return WheelionsApplication.getResourceUrl(getThumb());
		case Normal:
			return WheelionsApplication.getResourceUrl(getPreview());
		case Original:
			return WheelionsApplication.getResourceUrl(getOriginal());
		default:
			return WheelionsApplication.getResourceUrl(getPreview());
		}
	}
	@Override
	public String getImageID() {
		return getId();
	}

}
