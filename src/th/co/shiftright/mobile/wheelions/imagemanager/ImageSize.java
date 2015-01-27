package th.co.shiftright.mobile.wheelions.imagemanager;

public enum ImageSize {
	Thumb,
	Normal,
	Original;

	public static String toString(ImageSize size) {
		switch (size) {
		case Thumb:
			return "thumb";
		case Normal:
			return "normal";
		case Original:
			return "original";
		default:
			return "normal";
		}
	}

	public static ImageSize fromString(String size) {
		if (size.equals("thumb")) {
			return ImageSize.Thumb;
		} else if (size.equals("normal")) {
			return ImageSize.Normal;
		} else if (size.equals("original")) {
			return ImageSize.Original;
		} else {
			return ImageSize.Normal;
		}
	}
}
