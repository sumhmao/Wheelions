package th.co.shiftright.mobile.wheelions.api;

public class BitmapKeyValue {

	private String fileName;
	private byte[] bitmapData;

	public String getFileName() {
		return fileName;
	}

	public byte[] getBitmapData() {
		return bitmapData;
	}

	public BitmapKeyValue(String fileName, byte[]bitmapData) {
		this.fileName = fileName;
		this.bitmapData = bitmapData;
	}

}
