package com.androidcommonlibrary.crashhandler;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.androidcommonlibrary.R;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class ExceptionHandler implements
		Thread.UncaughtExceptionHandler  {
	private final Activity myContext;
	private final String LINE_SEPARATOR = "\n";
	StringBuilder errorReport;
	private final String SUBJECT=":: YOUR_APP_NAME CRASH REPORT ::";

	/*Use here your gmail credentials from which you want to send mail*/
	private final String FROMEMAIL ="youremailid@gmail.com";
	private final String FROMEMAILPSD ="yourpassword";

	/*Use credentials from whom you want to send mail*/
	private final String[] TOEMAIL = {"recieveremail@xyz.com"};

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

            try{
                SendEmailAsyncTask email = new SendEmailAsyncTask();
                email.m = new Mail(FROMEMAIL, FROMEMAILPSD);
                email.m.set_from(FROMEMAIL);
                email.m.setBody(errorReport.toString());
                email.m.set_to(TOEMAIL);
                email.m.set_subject(SUBJECT);
                email.execute();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
		Mail m;
		public SendEmailAsyncTask() {}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				if (m.send()) {
					Log.d("SendMail", "---SENT---");
				} else {
					Log.e("SendMail", "---FAILED!!---");
				}

				return true;
			} catch (AuthenticationFailedException e) {
				Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
				e.printStackTrace();
				return false;
			} catch (MessagingException e) {
				Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("SendMail", "---Unexpected error occured.!!---");
				return false;
			}
		}
	}
}