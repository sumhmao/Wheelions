package th.co.shiftright.mobile.wheelions.custom_controls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

	private GestureDetector mGestureDetector;
	protected View.OnTouchListener mGestureListener;

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CustomScrollView(Context context) {
		super(context);
		init(context);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
		setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {             
			return Math.abs(distanceY) > Math.abs(distanceX);
		}
	}

}
