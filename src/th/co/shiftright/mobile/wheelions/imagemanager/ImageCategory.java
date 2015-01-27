package th.co.shiftright.mobile.wheelions.imagemanager;

public enum ImageCategory {
	User;

	public static String toString(ImageCategory size) {
		switch (size) {
		case User:
			return "user";
		default:
			return "user";
		}
	}

	public static ImageCategory fromString(String size) {
		if (size.equals("user")) {
			return ImageCategory.User;
		} else {
			return ImageCategory.User;
		}
	}

}
