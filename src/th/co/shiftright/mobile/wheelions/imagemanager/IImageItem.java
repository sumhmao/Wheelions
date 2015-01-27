package th.co.shiftright.mobile.wheelions.imagemanager;

public interface IImageItem {

	public abstract ImageCategory getImageCategory();
	public abstract String getImageUrl(ImageSize size);
	public abstract String getImageID();

}
