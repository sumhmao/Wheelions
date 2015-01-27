package th.co.shiftright.mobile.wheelions.custom_controls;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.api.APIAsyncBackendCall;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class CustomProgressDialog extends ProgressDialog {

    private TextView headerTextView;
    private TextView detailTextView;
    private String headerText = null;
    private String detailText = null;
    private APIAsyncBackendCall request = null;

    public void setAsyncBackendCall(APIAsyncBackendCall request) {
        this.request = request;
    }

    public void setHeader(String headerText) {
        this.headerText = headerText;
    }

    public void setDetail(String detailText) {
        this.detailText = detailText;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        initializeComponents();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);
        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void initializeComponents() {
        headerTextView = (TextView) findViewById(R.id.dialogTitle);
        detailTextView = (TextView) findViewById(R.id.dialogMessage);
        setDialogTitle();
    }

    public void onLocalizationChanged() {
        setDialogTitle();
    }

    private void setDialogTitle() {
        if (headerTextView != null) {
            if (headerText != null) {
                headerTextView.setText(headerText);
            } else {
                headerTextView.setText("Now loading");
            }
        }
        if (detailTextView != null) {
            if (detailText != null) {
                detailTextView.setText(detailText);
            } else {
                detailTextView.setText("please wait...");
            }
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (request != null) {
            request.cancelTask();
        }
    }

}