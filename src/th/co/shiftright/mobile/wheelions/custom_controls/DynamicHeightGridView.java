package th.co.shiftright.mobile.wheelions.custom_controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ScrollView;

public class DynamicHeightGridView extends GridView {

	private int maximumHeight = 0;
	private ScrollView parentScrollView = null;
	private boolean isUnlimitedHeight = true;

	public void setUnlimitedHeight(boolean isUnlimitedHeight) {
		this.isUnlimitedHeight = isUnlimitedHeight;
	}

	public void setParentScrollView(ScrollView parentScrollView) {
		this.parentScrollView = parentScrollView;
	}

	public DynamicHeightGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialization();
	}

	public DynamicHeightGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialization();
	}

	public DynamicHeightGridView(Context context) {
		super(context);
		initialization();
	}

	private void initialization() {
		setMaximumHeight(304);
	}

	private int getDPFromPixels(int pixel) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (pixel * scale + 0.5f);
	}

	public void setMaximumHeight(int maxHeight) {
		this.maximumHeight = getDPFromPixels(maxHeight);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		if (isUnlimitedHeight) {
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, maximumHeight);	
		}
	}

	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isUnlimitedHeight) {
			if (parentScrollView != null) {
				parentScrollView.requestDisallowInterceptTouchEvent(true);
			}
			return super.onTouchEvent(ev);
		} else {
			return false;
		}
	}

}
