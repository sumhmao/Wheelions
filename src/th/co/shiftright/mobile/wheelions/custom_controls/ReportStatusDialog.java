package th.co.shiftright.mobile.wheelions.custom_controls;

import java.util.ArrayList;

import th.co.shiftright.mobile.wheelions.R;
import th.co.shiftright.mobile.wheelions.adapters.StatusSpinnerAdapter;
import th.co.shiftright.mobile.wheelions.models.TaskStatus;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class ReportStatusDialog extends Dialog {

	private ImageButton closeBtn;
	private Spinner snStatusSpinner;
	private ArrayList<TaskStatus> allStatus;
	private StatusSpinnerAdapter adapter;
	private Button btnCancel;
	private Button btnReport;

	private ReportStatusDialogListener listener = null;
	public void setReportStatusDialogListener(ReportStatusDialogListener listener) {
		this.listener = listener;
	}

	public ReportStatusDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (context instanceof Activity) {
			setOwnerActivity((Activity) context);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_status_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		allStatus = new ArrayList<TaskStatus>();
		allStatus.add(new TaskStatus("02", "รับของ"));
		allStatus.add(new TaskStatus("03", "รายงานตำแหน่ง"));
		allStatus.add(new TaskStatus("04", "ถึงปลายทาง"));
		allStatus.add(new TaskStatus("99", "ปิดงาน"));
		initializeTopPanel();
		initializeComponent();
	}

	private void initializeTopPanel() {
		closeBtn = (ImageButton) findViewById(R.id.btn_close_dialog);
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void initializeComponent() {
		snStatusSpinner = (Spinner) findViewById(R.id.snStatusSpinner);
		adapter = new StatusSpinnerAdapter(getContext(), allStatus);
		snStatusSpinner.setAdapter(adapter);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btnReport = (Button) findViewById(R.id.btnReport);
		btnReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (listener != null) {
					listener.onStatusReported((TaskStatus) snStatusSpinner.getSelectedItem());
				}
			}
		});
	}

}
