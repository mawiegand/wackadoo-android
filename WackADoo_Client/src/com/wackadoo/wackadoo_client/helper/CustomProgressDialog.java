package com.wackadoo.wackadoo_client.helper;

import android.app.ProgressDialog;
import android.content.Context;

import com.wackadoo.wackadoo_client.R;

public class CustomProgressDialog extends ProgressDialog {

	private Context context;
	
	// constructor for standard server communication dialog
	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
		setTitle(context.getResources().getString(R.string.server_communication));
		setMessage(context.getResources().getString(R.string.please_wait));
		setCancelable(false);
	}
	
	// show and style dialog
	@Override
	public void show() {
		super.show();
		StaticHelper.styleDialog(context, this);
	}
}
