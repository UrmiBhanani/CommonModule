package com.androidcommonlibrary.crashhandler;

import android.app.Activity;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.androidcommonlibrary.R;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ExceptionHandler implements
		Thread.UncaughtExceptionHandler  {
	private final Activity myContext;
	private final String LINE_SEPARATOR = "\n";
	GMailSender sender;
	StringBuilder errorReport;
	private final String SUBJECT=":: YOUR_APP_NAME CRASH REPORT ::";

	/*Use here your gmail credentials from which you want to send mail*/
	private final String FROMEMAIL ="youremailid@gmail.com";
	private final String FROMEMAILPSD ="yourpassword";

	/*Use credentials from whom you want to send mail*/
	private final String TOEMAIL ="recieveremail@xyz.com";

	public ExceptionHandler(Activity context) {
		myContext = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR for "+ myContext.getApplicationContext().getString(R.string.app_name)+" ************\n\n");
		errorReport.append("Localized Error Message: ");
		errorReport.append(exception.getLocalizedMessage());
		errorReport.append("Error Message: ");
		errorReport.append(exception.getMessage());
		errorReport.append("StackTrace");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

		try {
			if (Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}

			System.out.print(errorReport.toString());

			try {

				myContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try{
							sender = new GMailSender(FROMEMAIL, FROMEMAILPSD);
							sender.sendMail(SUBJECT,
									errorReport.toString(),
									FROMEMAIL,
									TOEMAIL);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});


			} catch (Exception e) {
				Log.e("SendMail", e.getMessage(), e);
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
}