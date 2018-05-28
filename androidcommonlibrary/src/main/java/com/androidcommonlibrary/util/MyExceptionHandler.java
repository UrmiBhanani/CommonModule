package com.androidcommonlibrary.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by admin1 on 25/8/17.
 */

public class MyExceptionHandler {

    private static MyExceptionHandler.ExceptionBuilder exceptionBuilder = null;
    private Exception exception = null;
    private Context context = null;

    private boolean isNeedToShowToast = false;
    private boolean isNeedToShowAlertDialogWithOneButton = false;
    private boolean isNeedToShowAlertDialogWithTwoButton = false;
    private boolean isCustomDialog = false;
    private boolean isCustomToast = false;
    private boolean isNeedToPrintLogs = false;
    private boolean isNeedToHandleAnyCrashAnalyticalReport = false;

    private String errorTag = null;
    private String buttonPositiveTitle = null;
    private String buttonNegativeTitle = null;
    private String singleButtonTitle = null;

    private CallBackTwoButtonAlertDialog callBackTwoButtonAlertDialog = null;
    private CallBackOneButtonAlertDialog callBackOneButtonAlertDialog = null;

    private MyExceptionHandler(ExceptionBuilder builder, Context context) {

        this.isNeedToShowAlertDialogWithOneButton = builder.isNeedToShowAlertDialogWithOneButton;
        this.isNeedToShowAlertDialogWithTwoButton = builder.isNeedToShowAlertDialogWithTwoButton;
        this.isNeedToShowToast = builder.isNeedToShowToast;
        this.isNeedToPrintLogs = builder.isNeedToPrintLogs;
        this.context = context;
        this.exception = builder.exception;
        this.isNeedToHandleAnyCrashAnalyticalReport = builder.isNeedToHandleAnyCrashAnalyticalReport;
        this.isCustomDialog = builder.isCustomDialog;
        this.isCustomToast = builder.isCustomToast;


        this.errorTag = builder.errorTag;
        this.buttonPositiveTitle = builder.buttonPositiveTitle;
        this.buttonNegativeTitle = builder.buttonNegativeTitle;
        this.singleButtonTitle = builder.singleButtonTitle;

        this.callBackOneButtonAlertDialog = builder.callBackOneButtonAlertDialog;
        this.callBackTwoButtonAlertDialog = builder.callBackTwoButtonAlertDialog;


        performAction(context, exception);


    }

    public static MyExceptionHandler.ExceptionBuilder getInstance() {
        if (exceptionBuilder == null) {
            exceptionBuilder = new MyExceptionHandler.ExceptionBuilder();
        }
        return exceptionBuilder;
    }


    private void performAction(Context context, Exception e) {

        try {
            //checking display dialog or toast message
            this.context = context;
            this.exception = e;


            if (this.context != null) {

                if (isCustomToast && isCustomDialog) {
                    displayCustomToastMessage();
                } else if (isCustomDialog) {
                    displayCustomAlertDialog();
                } else if (isCustomToast) {
                    displayCustomToastMessage();
                } else {
                    if (isNeedToShowAlertDialogWithOneButton && isNeedToShowAlertDialogWithTwoButton && isNeedToShowToast) {
                        displayToastMessage();
                    } else if (isNeedToShowAlertDialogWithTwoButton && isNeedToShowAlertDialogWithOneButton) {
                        displayAlertDialogWithOneButton();

                    } else if (isNeedToShowAlertDialogWithOneButton) {
                        displayAlertDialogWithOneButton();
                    } else if (isNeedToShowAlertDialogWithTwoButton) {
                        displayAlertDialogWithTwoButton();
                    } else if (isNeedToShowToast) {
                        displayToastMessage();
                    }

                    if (isNeedToHandleAnyCrashAnalyticalReport) {
                        handleCrashReport();
                    }

                }


            }

            if (isNeedToPrintLogs) {
                displayPrintLogs();
            }




        } catch (Exception e1) {
            e.printStackTrace();

        }


    }

    //write here code for displaying toast message
    private void displayToastMessage() {

        if (this.exception != null) {

            Toast.makeText(context, "Toast message Message =" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayCustomToastMessage() {

        if (this.exception != null) {

            Toast.makeText(context, "Custom Toast message Message =" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayCustomAlertDialog() {

        if (this.exception != null) {

            Toast.makeText(context, "Custom Alert message Message =" + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //write here code for displaying alert dialog
    private void displayAlertDialogWithOneButton() {
        if (this.context != null) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            String buttonPositive = null;
            if (Utils.isNotNull(singleButtonTitle)) {
                buttonPositive = this.singleButtonTitle;
            } else {
                buttonPositive = context.getResources().getString(android.R.string.yes);
            }


            builder.setTitle("Set Title")
                    .setMessage("Set Message")
                    .setPositiveButton(buttonPositive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete

                            if (callBackOneButtonAlertDialog != null) {
                                callBackOneButtonAlertDialog.onPositionButtonClicked();
                            }
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


    }


    private void displayAlertDialogWithTwoButton() {
        if (this.context != null) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }

            String buttonPositive = null, buttonNegative = null;
            if (Utils.isNotNull(buttonPositiveTitle)) {
                buttonPositive = this.buttonPositiveTitle;
            } else {
                buttonPositive = context.getResources().getString(android.R.string.yes);
            }
            if (Utils.isNotNull(buttonNegativeTitle)) {
                buttonNegative = this.buttonNegativeTitle;
            } else {
                buttonNegative = context.getResources().getString(android.R.string.no);
            }


            builder.setTitle("Set Title")
                    .setMessage("Set Message")
                    .setPositiveButton(buttonPositive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (callBackTwoButtonAlertDialog != null) {
                                callBackTwoButtonAlertDialog.onPositionButtonClicked();
                            }
                            // continue with delete
                        }
                    })
                    .setNegativeButton(buttonNegative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing

                            if (callBackTwoButtonAlertDialog != null) {
                                callBackTwoButtonAlertDialog.onNegativeButtonClicked();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


    //write here code for displaying alert dialog
    private void displayPrintLogs() {
        if (isNeedToPrintLogs) {
            if (Log.DO_LOGGING) {
                if (this.exception != null) {
                    Log.print(exception.getMessage());
                }

            }
        }
    }

    //write here code for displaying alert dialog
    private void handleCrashReport() {

        if (errorTag != null) {
            Toast.makeText(context, "Toast message Message =" + errorTag, Toast.LENGTH_LONG).show();

        }



    }




    public interface CallBackTwoButtonAlertDialog {

        void onNegativeButtonClicked();

        void onPositionButtonClicked();

    }

    public interface CallBackOneButtonAlertDialog {
        void onPositionButtonClicked();
    }

    public static class ExceptionBuilder {
        private boolean isNeedToShowToast = false;
        private boolean isNeedToShowAlertDialogWithOneButton = false;
        private boolean isNeedToShowAlertDialogWithTwoButton = false;
        private boolean isNeedToPrintLogs = false;
        private boolean isNeedToHandleAnyCrashAnalyticalReport = false;

        private CallBackTwoButtonAlertDialog callBackTwoButtonAlertDialog = null;
        private CallBackOneButtonAlertDialog callBackOneButtonAlertDialog = null;

        private String buttonPositiveTitle = null;
        private String buttonNegativeTitle = null;
        private String singleButtonTitle = null;
        private String errorTag = null;


        private Exception exception = null;

        private boolean isCustomDialog = false;
        private boolean isCustomToast = false;


        public ExceptionBuilder isCustomToast(boolean isCustomToast) {
            this.isCustomToast = isCustomToast;
            return this;
        }

        public ExceptionBuilder isCustomDialog(boolean isCustomDialog) {
            this.isCustomDialog = isCustomDialog;
            return this;
        }


        public ExceptionBuilder setbuttonPositiveTitle(String buttonPositiveTitle) {
            this.buttonPositiveTitle = buttonPositiveTitle;
            return this;
        }

        public ExceptionBuilder setbuttonNegativeTitle(String buttonNegativeTitle) {
            this.buttonNegativeTitle = buttonNegativeTitle;
            return this;
        }

        public ExceptionBuilder setsingleButtonTitle(String singleButtonTitle) {
            this.singleButtonTitle = singleButtonTitle;
            return this;
        }


        public ExceptionBuilder callBackOneButtonAlertDialog(CallBackOneButtonAlertDialog callBackOneButtonAlertDialog) {
            this.callBackOneButtonAlertDialog = callBackOneButtonAlertDialog;
            return this;
        }

        public ExceptionBuilder callBackTwoButtonAlertDialog(CallBackTwoButtonAlertDialog callBackTwoButtonAlertDialog) {
            this.callBackTwoButtonAlertDialog = callBackTwoButtonAlertDialog;
            return this;
        }




        public ExceptionBuilder setExceptionTag(String errorTag) {
            this.errorTag = errorTag;
            return this;
        }

        public ExceptionBuilder setException(Exception e) {
            this.exception = e;
            return this;
        }

        public ExceptionBuilder isNeedToShowToast(boolean isNeedToShowToast) {
            this.isNeedToShowToast = isNeedToShowToast;
            return this;
        }

        public ExceptionBuilder isNeedToHandleAnyCrashAnalyticalReport(boolean isNeedToHandleAnyCrashAnalyticalReport) {
            this.isNeedToHandleAnyCrashAnalyticalReport = isNeedToHandleAnyCrashAnalyticalReport;
            return this;
        }

        public ExceptionBuilder isNeedToShowAlertDialogWithOneButton(boolean isNeedToShowAlertDialogWithOneButton) {
            this.isNeedToShowAlertDialogWithOneButton = isNeedToShowAlertDialogWithOneButton;
            return this;
        }

        public ExceptionBuilder isNeedToShowAlertDialogWithTwoButton(boolean isNeedToShowAlertDialogWithTwoButton) {
            this.isNeedToShowAlertDialogWithTwoButton = isNeedToShowAlertDialogWithTwoButton;
            return this;
        }

        public ExceptionBuilder isNeedToPrintLogs(boolean isNeedToPrintLogs) {
            this.isNeedToPrintLogs = isNeedToPrintLogs;
            return this;
        }

        public MyExceptionHandler build(Context context) {


            return new MyExceptionHandler(this, context);

        }

        public MyExceptionHandler build() {


            return new MyExceptionHandler(this, null);

        }

    }
}
