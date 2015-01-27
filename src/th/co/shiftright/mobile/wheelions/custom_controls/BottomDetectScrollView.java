package th.co.shiftright.mobile.wheelions.custom_controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class BottomDetectScrollView extends ScrollView {

	private BottomDetectScrollViewListener listener = null;

	public void setBottomDetectScrollViewListener(BottomDetectScrollViewListener listener) {
		this.listener = listener;
	}

	public BottomDetectScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public BottomDetectScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomDetectScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		View view = (View) getChildAt(getChildCount()-1);
		int diff = (view.getBottom() - (getHeight() + getScrollY()));
		if (diff <= 0 && listener != null) {
			listener.onBottomReached();
		}
		if (listener != null) {
			listener.onScrollChanged(l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
