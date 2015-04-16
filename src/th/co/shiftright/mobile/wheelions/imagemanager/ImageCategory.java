package th.co.shiftright.mobile.wheelions.imagemanager;

public enum ImageCategory {
	User,
	TaskLog,
	StaticMap;

	public static String toString(ImageCategory size) {
		switch (size) {
		case User:
			return "user";
		case TaskLog:
			return "tasklog";
		case StaticMap:
			return "staticmap";
		default:
			return "user";
		}
	}

	public static ImageCategory fromString(String size) {
		if (size.equals("user")) {
			return ImageCategory.User;
		} else if (size.equals("tasklog")) {
			return ImageCategory.TaskLog;
		} else if (size.equals("staticmap")) {
			return ImageCategory.StaticMap;
		} else {
			return ImageCategory.User;
		}
	}

}
