package com.androidcommonlibrary.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcommonlibrary.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("NewApi")
public class Utils {
    public final static int DEFAULT_DELAY = 0;
    public final static int COLOR_ANIMATION_DURATION = 1000;
    public static int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed}  // pressed
    };
    private static Dialog dialog = null;
    Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    /*
     * set ListView height based on height of childen
     */
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
                MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listAdapter.getCount()));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * @param cr
     * @param id
     * @param photo_id
     * @return
     * @author ubbvand2 Himanshu Method use to load Photo of Contact it will
     * load Photo Either from Contact _Id or from PhotoId
     */
    public static Bitmap loadContactPhoto(ContentResolver cr, long id,
                                          long photo_id) {

        Uri uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts
                .openContactPhotoInputStream(cr, uri);
        if (input != null) {
            return BitmapFactory.decodeStream(input);
        } else {
            // Log.d("PHOTO","first try failed to load photo");

        }

        byte[] photoBytes = null;

        Uri photoUri = ContentUris.withAppendedId(
                ContactsContract.Data.CONTENT_URI, photo_id);

        Cursor c = cr.query(photoUri,
                new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO},
                null, null, null);

        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        } finally {

            c.close();
        }

        if (photoBytes != null)
            return BitmapFactory.decodeByteArray(photoBytes, 0,
                    photoBytes.length);
        else
            // Log.d("PHOTO","second try also failed");
            return null;
    }

    public static Animation expand(final View v, final boolean expand) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class,
                    int.class);
            m.setAccessible(true);
            m.invoke(v,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(
                            ((View) v.getParent()).getMeasuredWidth(),
                            MeasureSpec.AT_MOST));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
            v.setVisibility(View.GONE);
        } else {
            v.getLayoutParams().height = initialHeight;
            v.setVisibility(View.VISIBLE);
        }

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                int newHeight = 0;

                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }

                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand)
                    v.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (initialHeight / v.getContext().getResources()
                .getDisplayMetrics().density));// SPEED_ANIMATION_TRANSITION

        return a;
    }

    public static boolean isNotNullOrBlank(String mString) {
        if (mString == null) {
            return false;
        } else if (mString.equalsIgnoreCase("")) {
            return false;
        } else if (mString.equalsIgnoreCase("null")) {
            return false;
        } else return !mString.equalsIgnoreCase(null);
    }

    /*for open other application*/
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    /**
     * Set Ripple effect to view
     * param mContext: context of screen
     * param view: View
     */
    public static void setRippleEffect(Context mContext, View view) {
        int[] colors = new int[]{
                mContext.getResources().getColor(R.color.colorAccent),
                mContext.getResources().getColor(R.color.colorAccent),
                Color.GREEN,
                Color.WHITE
        };
        ColorStateList myList = new ColorStateList(Utils.states, colors);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (view instanceof AppCompatButton) {
                ((AppCompatButton) view).setSupportBackgroundTintList(myList);
            } else if (view instanceof AppCompatButton) {
                ((FloatingActionButton) view).setRippleColor(mContext.getResources().getColor(R.color.colorAccent));
            } else {
                ViewCompat.setBackgroundTintList(view, myList);
                AlphaAnimation obja = new AlphaAnimation(1.0f, 0.3f);
                obja.setDuration(5);
                obja.setFillAfter(false);
            }
        } else {
            ViewCompat.setBackgroundTintList(view, myList);
            AlphaAnimation obja = new AlphaAnimation(1.0f, 0.3f);
            obja.setDuration(5);
            obja.setFillAfter(false);
        }
    }

    /**
     * Set Ripple effect to view
     * param mContext: context of screen
     * param view: View
     */
    public static void setRippleEffect(Context mContext, android.support.design.widget.FloatingActionButton view) {
        int[] colors = new int[]{
                mContext.getResources().getColor(R.color.colorPrimary),
                mContext.getResources().getColor(R.color.colorPrimary),
                mContext.getResources().getColor(R.color.colorPrimaryDark),
                Color.WHITE
        };
        ColorStateList myList = new ColorStateList(Utils.states, colors);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackgroundTintList(myList);
        } else {
            view.setBackgroundTintList(myList);
            ViewCompat.setBackgroundTintList(view, myList);
        }
    }

    /**
     * Change the color of a view with an animation
     *
     * @param v          the view to change the color
     * @param startColor the color to start animation
     * @param endColor   the color to end the animation
     */
    public static void animateViewColor(View v, int startColor, int endColor) {

        ObjectAnimator animator = ObjectAnimator.ofObject(v, "backgroundColor",
                new ArgbEvaluator(), startColor, endColor);

//        animator.setInterpolator(new PathInterpolator(0.4f,0f,1f,1f));
        animator.setDuration(COLOR_ANIMATION_DURATION);
        animator.start();
    }

    public static boolean isOnline(Context context) {

        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                return cm.getActiveNetworkInfo().isConnectedOrConnecting();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showSnackbar(Context context, String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(4);  // show multiple line
        snackbar.show();
    }
    /* Check Internet Connectivity */

    public static void showSnackbarLong(Context context, String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    @SuppressLint("NewApi")
    public static int getDeviceWidth(Context mContext) {
        Display display = ((WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    @SuppressLint("NewApi")
    public static int getDeviceHeight(Context mContext) {
        Display display = ((WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(),
                    bitmap.getHeight(), Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(),
                    Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap cropImage(Bitmap bitMap, int Width, int Height) {
        int bitWidth = bitMap.getWidth();
        int bitHeight = bitMap.getHeight();

        int X = 0;
        int Y = 0;

        if (bitHeight < bitWidth) {
            X = (bitWidth / 2) - (Width / 2);
        } else {
            Y = (bitHeight / 2) - (Height / 2);
        }

        if ((X + Width) <= bitWidth && (Y + Height) <= bitHeight)
            return Bitmap.createBitmap(bitMap, X, Y, Width, Height);
        else
            return bitMap;
    }

    public static Bitmap resize(Bitmap bitMap, int width, int height) {

        int per;
        int bitWidth = bitMap.getWidth();
        int bitHeight = bitMap.getHeight();

        if (bitHeight < bitWidth) {
            per = (height * 100) / bitHeight;
            bitHeight = height;
            bitWidth = (bitWidth * per) / 100;
        } else {
            per = (width * 100) / bitWidth;
            bitWidth = width;
            bitHeight = (bitHeight * per) / 100;
        }
        return Bitmap.createScaledBitmap(bitMap, bitWidth, bitHeight, false);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String getLastSeenText(long last_seen) {

        String LAST_SEEN_STRING = "";

        long todays_start_millis = Utils.convertStringToDate(
                Utils.convertDateToString(new Date(), "dd/MM/yyyy"),
                "dd/MM/yyyy").getTime();

        long todays_end_millis = todays_start_millis + 1000 * 60 * 60 * 24;

        long yesterday_start_millis = todays_start_millis - 1000 * 60 * 60 * 24;

        long yesterday_end_millis = todays_start_millis - 1000;

        long currMillis = new Date().getTime();

        if (last_seen < currMillis + 1000 * 30
                && last_seen > currMillis - 1000 * 30) {
            LAST_SEEN_STRING = "Online";
        } else if (last_seen > todays_start_millis
                && last_seen < todays_end_millis) {
            LAST_SEEN_STRING = "Last Seen Today at "
                    + Utils.millisToDate(last_seen, "hh:mm aa");
        } else if (last_seen > yesterday_start_millis
                && last_seen < yesterday_end_millis) {
            LAST_SEEN_STRING = "Last Seen Yesterday at "
                    + Utils.millisToDate(last_seen, "hh:mm aa");
        } else {
            LAST_SEEN_STRING = "Last Seen at "
                    + Utils.millisToDate(last_seen, "dd/MM/yyyy hh:mm aa");
        }
        Log.print("LAST_SEEN_STRING  :: " + LAST_SEEN_STRING);
        return LAST_SEEN_STRING;
    }

    public static long dateToMillis(String dateString, String format) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date mDate = sdf.parse(dateString);
            timeInMilliseconds = mDate.getTime();
            Log.print("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String millisToDate(long millis, String format) {

        return new SimpleDateFormat(format).format(new Date(millis));
    }

    public static String convertDateToString(Date objDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).format(objDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

	/* Return LastSeen with Date&Time */

    public static Date convertStringToDate(String strDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	/* Date To Millis */

    public static String getDate(long milliSeconds, String format) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        Date date = new Date(milliSeconds);
        SimpleDateFormat dateformat = new SimpleDateFormat(format);
        System.out.println(dateformat.format(date));
        return dateformat.format(date);
    }

	/* Millis To Date */

    public static String getTimeAgo(Date date, Context ctx) {

        try {
            if (date == null) {
                return null;
            }

            long time = date.getTime();

            Date curDate = new Date();
            long now = curDate.getTime();
            if (time > now || time <= 0) {
                return null;
            }

            int dim = getTimeDistanceInMinutes(time);

            String timeAgo = null;

            if (dim == 0) {
                timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_minute);
            } else if (dim == 1) {
                return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
            } else if (dim >= 2 && dim <= 44) {
                timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
            } else if (dim >= 45 && dim <= 89) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_an) + " " + ctx.getResources().getString(R.string.date_util_unit_hour);
            } else if (dim >= 90 && dim <= 1439) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + ctx.getResources().getString(R.string.date_util_unit_hours);
            } else if (dim >= 1440 && dim <= 2519) {
                timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
            } else if (dim >= 2520 && dim <= 43199) {
                timeAgo = (Math.round(dim / 1440)) + " " + ctx.getResources().getString(R.string.date_util_unit_days);
            } else if (dim >= 43200 && dim <= 86399) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_month);
            } else if (dim >= 86400 && dim <= 525599) {
                timeAgo = (Math.round(dim / 43200)) + " " + ctx.getResources().getString(R.string.date_util_unit_months);
            } else if (dim >= 525600 && dim <= 655199) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
            } else if (dim >= 655200 && dim <= 914399) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " " + ctx.getResources().getString(R.string.date_util_term_a) + " " + ctx.getResources().getString(R.string.date_util_unit_year);
            } else if (dim >= 914400 && dim <= 1051199) {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + ctx.getResources().getString(R.string.date_util_unit_years);
            } else {
                timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + ctx.getResources().getString(R.string.date_util_unit_years);
            }

            return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "";
        }
    }

	/* Date to String */

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = new Date().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

	/* String to Date */

    public static void hideSoftKeyboard(Activity activity) {

		/*InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);*/

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

	/* Milliseconds to date in String */

    public static int convertDpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

	/* Return Last Updated  */

    public static int convertPxToDp(Context context, float px) {
        final int scale = (int) (px / context.getResources()
                .getDisplayMetrics().density);


        return scale;
    }

    /**
     * Copies text into Clip Board
     *
     * @param mContext
     * @param mText
     */
    public static void copyTextToClipBoard(Context mContext, String mText) {
        ClipboardManager myClipboard = (ClipboardManager) mContext
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText(mContext.getResources().getString(R.string.app_name), mText);
        myClipboard.setPrimaryClip(myClip);
    }

	/* Hide/Close SoftKeyboard */

    /*
     * set ListView height based on height of childen
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
                MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
            Log.print("" + totalHeight);
        }

        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

	/* Convert Dp to Pixel */

    /**
     * Returns MD5 of a string
     */
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32
            // chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showAlertDialog(Context context, String mMsgString) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(mMsgString);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.show();

    }

    public static void showErrorAlertDialog(final Context context, String mMsgString, final View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(context.getResources().getString(R.string.app_name));
        builder.setMessage(mMsgString);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (view != null) {
                    view.requestFocus();
                    showKeyboard(context, view);
                }

            }
        });
        builder.show();

    }

    public static void setEnlargeHitArea(final Button mButton) {
        try {
            final View parent = (View) mButton.getParent();  // button: the view you want to enlarge hit area
            parent.post(new Runnable() {
                public void run() {
                    final Rect rect = new Rect();
                    mButton.getHitRect(rect);
                    rect.top -= 100;    // increase top hit area
                    rect.left -= 100;   // increase left hit area
                    rect.bottom += 100; // increase bottom hit area
                    rect.right += 100;  // increase right hit area
                    parent.setTouchDelegate(new TouchDelegate(rect, mButton));
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setEnlargeHitArea(final TextView mButton) {
        try {
            final View parent = (View) mButton.getParent();  // button: the view you want to enlarge hit area
            parent.post(new Runnable() {
                public void run() {
                    final Rect rect = new Rect();
                    mButton.getHitRect(rect);
                    rect.top -= 50;    // increase top hit area
                    rect.left -= 150;   // increase left hit area
                    rect.bottom += 100; // increase bottom hit area
                    rect.right += 150;  // increase right hit area
                    parent.setTouchDelegate(new TouchDelegate(rect, mButton));
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setEnlargeHitArea(final ImageView mButton) {
        try {
            final View parent = (View) mButton.getParent();  // button: the view you want to enlarge hit area
            parent.post(new Runnable() {
                public void run() {
                    final Rect rect = new Rect();
                    mButton.getHitRect(rect);
                    rect.top -= 100;    // increase top hit area
                    rect.left -= 100;   // increase left hit area
                    rect.bottom += 100; // increase bottom hit area
                    rect.right += 100;  // increase right hit area
                    parent.setTouchDelegate(new TouchDelegate(rect, mButton));
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String convertDateFormate(String dateString, String inputDateString, String outputDateString) {
        Date date;
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(inputDateString);
        try {
            date = dateFormatLocal.parse(dateString);
            return new SimpleDateFormat(outputDateString).format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isNotNull(String mString) {
        if (mString == null) {
            return false;
        } else if (mString.equalsIgnoreCase("")) {
            return false;
        } else if (mString.equalsIgnoreCase("N/A")) {
            return false;
        } else if (mString.equalsIgnoreCase("[]")) {
            return false;
        } else if (mString.equalsIgnoreCase("null")) {
            return false;
        } else return !mString.equalsIgnoreCase("{}");
    }

    public static void expand(final View v) {

        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int measuredHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (measuredHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp per milliseconds
        a.setDuration((int) (measuredHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp per milliseconds
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void setTextOnly(EditText mEditText) {
        mEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) { // for backspace
                            return src;
                        }
                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }
                        return "";
                    }
                }
        });
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Hide keyboard if user touch outside editText..
     * param view: parent view
     * param mContext: context
     */
    public static void setupOutSideTouchHideKeyboard(final View view, final Context mContext) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager mgr = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupOutSideTouchHideKeyboard(innerView, mContext);
            }
        }
    }

    public static void showKeyboard(Context context, View v) {
        /*InputMethodManager mgr = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(v, 0);*/

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

    }

    static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static RatingBar getRatingBar(Context _Context,
                                         float defaultRating, int mId) {

        RatingBar mRatingBar = new RatingBar(_Context, null,
                R.style.MyRatingBarSmall);

        LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor(_Context.getResources().getString(R.string.rating_fill)), PorterDuff.Mode.SRC_ATOP);

        mRatingBar.setTag(mId);

        mRatingBar.setStepSize(1);
        mRatingBar.setNumStars(5);

        mRatingBar.setRating(defaultRating);

        LinearLayout.LayoutParams mLl = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        mRatingBar.setLayoutParams(mLl);

        return mRatingBar;
    }

    public static TextView getEmptyTextView(Context _Context) {
        TextView mTxtView = new TextView(_Context);
        mTxtView.setTextSize(16);
        mTxtView.setText("No records found");
        mTxtView.setGravity(Gravity.CLIP_HORIZONTAL);
        mTxtView.setTextColor(_Context.getResources().getColor(R.color.colorPrimary, null));

        return mTxtView;
    }



    /**
     * for Android M+
     * check whether permission is enable or not
     */
    public static boolean checkNeedsPermission(Context mContext, String permission) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED;
    }



    public static String replaceString(String text) {

        if (text.contains("&amp;"))
            text = text.replace("&amp;", "&");
        if (text.contains("&quot;"))
            text = text.replace("&quot;", "\"");
        if (text.contains("&#39;"))
            text = text.replace("&#39;", "'");
        if (text.contains("&#180;"))
            text = text.replace("&#180;", "'");
//		if(text.contains("’"))
//			text = text.replace("’", "'");
        if (text.contains("&gt;"))
            text = text.replace("&gt;", ">");
        if (text.contains("<br>"))
            text = text.replace("<br>", "\n");
        if (text.contains("&lt;"))
            text = text.replace("&lt;", "<");
        if (text.contains("&nbsp;"))
            text = text.replace("&nbsp;", " ");
        if (text.contains("\\/"))
            text = text.replace("\\/", "/");
//		if(text.contains("\n"))
//			text =text.replace("\n", "<br>");
        if (text.contains("\r\n"))
            text = text.replace("\r\n", "<br/>");
        /*if(text.contains("\n"))
            text = text.replace("\n", "<br/>");*/
		/*if(text.contains(""))
			text = text.replace("\"", "");*/

        return text;
    }

    public static String getRealPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isValidUrl(String txtWebsite) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
//        String pattern="(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$";
        return Patterns.WEB_URL.matcher(txtWebsite).matches();
    }

    public static Intent newEmailIntent(Context context, String address,
                                        String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    public static int convertToDensityPixels(Context context, int densityPixels) {
        float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (densityPixels * scale + 0.5f);
        return pixels;
    }

    public static String getDomainName(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return url;
        }
        String domain = uri.getHost();
        if (domain == null) {
            return url;
        }
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }


    /**
     * @param context     The new base context for this wrapper.
     * @param url         Url of where is image exist.
     * @param imageView   Image will display on this view.
     * @param placeHolder Placeholders are Drawables that are shown while a request is in progress.
     *                    When a request completes successfully, the placeholder is replaced with the requested resource.
     *                    If the requested resource is loaded from memory, the placeholder may never be shown.
     *                    If the request fails and an error Drawable is not set, the placeholder will continue to be displayed.
     *                    Similarly if the requested url/model is null and neither an error Drawable nor a fallback Drawable are set, the placeholder will also continue to be displayed.
     * @param errorHolder Error Drawables are shown when a request permanently fails. Error Drawables are also shown if the requested url/model is null and no fallback.
     */

    public static void loadImageWithGlide(Context context, String url, ImageView imageView, int placeHolder, int errorHolder) {

        try {
            Glide.with(context)
                    .load(url).placeholder(placeHolder).error(errorHolder).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param context     The new base context for this wrapper.
     * @param url         Url of where is image exist.
     * @param imageView   Image will display on this view.
     * @param placeHolder Placeholders are Drawables that are shown while a request is in progress.
     *                    When a request completes successfully, the placeholder is replaced with the requested resource.
     *                    If the requested resource is loaded from memory, the placeholder may never be shown.
     *                    If the request fails and an error Drawable is not set, the placeholder will continue to be displayed.
     *                    Similarly if the requested url/model is null and neither an error Drawable nor a fallback Drawable are set, the placeholder will also continue to be displayed.
     * @param errorHolder Error Drawables are shown when a request permanently fails. Error Drawables are also shown if the requested url/model is null and no fallback.
     * @param fallback    Fallback Drawables are shown when the requested url/model is null. The primary purpose of fallback Drawables is to allow users to indicate whether or not null is expected.
     *                    For example, a null profile url may indicate that the user has not set a profile photo and that a default should be used. However,
     *                    null may also indicate that meta-data is invalid or couldn’t be retrieved.
     *                    By default Glide treats null urls/models as errors, so users who expect null should set a fallback Drawable.
     */

    public static void loadImageWithGlide(Context context, String url, ImageView imageView, int placeHolder, int errorHolder, int fallback) {
        try {
            Glide.with(context)
                    .load(url).placeholder(placeHolder).error(errorHolder).fallback(fallback).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param context           The new base context for this wrapper.
     * @param url               Url of where is image exist.
     * @param imageView         Image will display on this view.
     * @param placeHolder       Placeholders are Drawables that are shown while a request is in progress.
     *                          When a request completes successfully, the placeholder is replaced with the requested resource.
     *                          If the requested resource is loaded from memory, the placeholder may never be shown.
     *                          If the request fails and an error Drawable is not set, the placeholder will continue to be displayed.
     *                          Similarly if the requested url/model is null and neither an error Drawable nor a fallback Drawable are set, the placeholder will also continue to be displayed.
     * @param errorHolder       Error Drawables are shown when a request permanently fails. Error Drawables are also shown if the requested url/model is null and no fallback.
     * @param diskCacheStrategy The available strategies allow you to prevent your load from using or writing to the disk cache or choose to cache only the unmodified original data backing your load,
     *                          only the transformed thumbnail produced by your load, or both.
     *                          DiskCacheStrategy.ALL  Caches remote data with both DATA and RESOURCE, and local data with RESOURCE only.
     *                          DiskCacheStrategy.AUTOMATIC  Tries to intelligently choose a strategy based on the data source of the DataFetcher and the EncodeStrategy of the ResourceEncoder (if an ResourceEncoder is available).
     *                          DiskCacheStrategy.DATA  Writes retrieved data directly to the disk cache before it's decoded.
     *                          DiskCacheStrategy.NONE  Saves no data to cache.
     *                          DiskCacheStrategy.RESOURCE  Writes resources to disk after they've been decoded.
     */

    public static void loadImageWithGlide(Context context, String url, ImageView imageView, int placeHolder, int errorHolder, DiskCacheStrategy diskCacheStrategy) {

        try {
            Glide.with(context)
                    .load(url).placeholder(placeHolder).error(errorHolder).diskCacheStrategy(diskCacheStrategy).skipMemoryCache(true)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param context                 The new base context for this wrapper.
     * @param url                     Url of where is image exist.
     * @param imageView               Image will display on this view.
     * @param placeHolder             Placeholders are Drawables that are shown while a request is in progress.
     *                                When a request completes successfully, the placeholder is replaced with the requested resource.
     *                                If the requested resource is loaded from memory, the placeholder may never be shown.
     *                                If the request fails and an error Drawable is not set, the placeholder will continue to be displayed.
     *                                Similarly if the requested url/model is null and neither an error Drawable nor a fallback Drawable are set, the placeholder will also continue to be displayed.
     * @param errorHolder             Error Drawables are shown when a request permanently fails. Error Drawables are also shown if the requested url/model is null and no fallback.
     * @param isSkipMemoryCacheEnable if it is false , will store image in cache for fast retrieve if it is true then skip to store image in cache memory.
     */

    public static void loadImageWithGlide(Context context, String url, ImageView imageView, int placeHolder, int errorHolder, boolean isSkipMemoryCacheEnable) {

        try {
            Glide.with(context)
                    .load(url).placeholder(placeHolder).error(errorHolder).skipMemoryCache(isSkipMemoryCacheEnable).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param context                 The new base context for this wrapper.
     * @param url                     Url of where is image exist.
     * @param imageView               Image will display on this view.
     * @param placeHolder             Placeholders are Drawables that are shown while a request is in progress.
     *                                When a request completes successfully, the placeholder is replaced with the requested resource.
     *                                If the requested resource is loaded from memory, the placeholder may never be shown.
     *                                If the request fails and an error Drawable is not set, the placeholder will continue to be displayed.
     *                                Similarly if the requested url/model is null and neither an error Drawable nor a fallback Drawable are set, the placeholder will also continue to be displayed.
     * @param errorHolder             Error Drawables are shown when a request permanently fails. Error Drawables are also shown if the requested url/model is null and no fallback.
     * @param isSkipMemoryCacheEnable if it is false , will store image in cache for fast retrieve if it is true then skip to store image in cache memory.
     */

    public static void loadImageWithGlide(Context context, String url, ImageView imageView, int placeHolder, int errorHolder, boolean isSkipMemoryCacheEnable, DiskCacheStrategy diskCacheStrategy) {

        try {
            Glide.with(context)
                    .load(url).fitCenter().placeholder(placeHolder).error(errorHolder).skipMemoryCache(isSkipMemoryCacheEnable).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param context                             The new base context for this wrapper.
     * @param url                                 Url of where is image exist.
     * @param imageView                           Image will display on this view.
     * @param placeHolder                         Placeholders are Drawables that are shown while a request is in progress.
     *                                            When a request completes successfully, the placeholder is replaced with the requested resource.
     *                                            If the requested resource is loaded from memory, the placeholder may never be shown.
     *                                            If the request fails and an error Drawable is not set, the placeholder will continue to be displayed.
     *                                           Similarly if the requested url/model is null and neither an error Drawable nor a fallback Drawable are set, the placeholder will also continue to be displayed.
     * @param errorHolder                        Error Drawables are shown when a request permanently fails. Error Drawables are also shown if the requested url/model is null and no fallback.
     * @param isSkipMemoryCacheEnable             if it is false , will store image in cache for fast retrieve if it is true then skip to store image in cache memory.
     * @param diskCacheStrategy                   The available strategies allow you to prevent your load from using or writing to the disk cache or choose to cache only the unmodified original data backing your load,
     *                                            only the transformed thumbnail produced by your load, or both.
     *                                            DiskCacheStrategy.ALL  Caches remote data with both DATA and RESOURCE, and local data with RESOURCE only.
     *                                            DiskCacheStrategy.AUTOMATIC  Tries to intelligently choose a strategy based on the data source of the DataFetcher and the EncodeStrategy of the ResourceEncoder (if an ResourceEncoder is available).
     *                                            DiskCacheStrategy.DATA  Writes retrieved data directly to the disk cache before it's decoded.
     *                                            DiskCacheStrategy.NONE  Saves no data to cache.
     *                                            DiskCacheStrategy.RESOURCE  Writes resources to disk after they've been decoded.
     * @param stringGlideDrawableRequestListener  Glide doesn't offer direct access to the GenericRequest class to set the logging, but you can catch the exception in
     *                                            case something goes wrong with the request. For example, if an image is not available, Glide would (silently) throw an
     *                                            exception and show the drawable you've specified in .error(). If you explicitly want to know the exception,
     *                                            create a listener and pass it to the .listener() method on the Glide builder.

     */

    public static void loadImageWithGlide(Context context, String url, ImageView imageView, int placeHolder, int errorHolder, boolean isSkipMemoryCacheEnable, DiskCacheStrategy diskCacheStrategy, RequestListener<String, GlideDrawable> stringGlideDrawableRequestListener) {

        try {
            Glide.with(context)
                    .load(url).fitCenter().placeholder(placeHolder).error(errorHolder).
                    skipMemoryCache(isSkipMemoryCacheEnable).diskCacheStrategy(diskCacheStrategy).listener(stringGlideDrawableRequestListener)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Show Dialog During web service calling
     */
    public void showProgressBar() {
        dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCancelable(false);

		/*Add any custom layout as per your requirement and uncomment below code*/

		/*LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewChild = inflater.inflate(R.layout.loader, null);

		dialog.setContentView(viewChild);*/

        Runtime.getRuntime().gc();
        System.gc();

        try {
            dialog.show();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * Hide progress dialog
     */
    public void hideProgressBar() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    // Compress image
    public String compressImage(String imageUri) {

        String filePath = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.debug("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.debug("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.debug("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.debug("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = imageUri.substring(imageUri.lastIndexOf("/"), imageUri.length());
//        getFilename();
        try {
            out = new FileOutputStream(imageUri);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    // calculate inSample size
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getDeviceFullInfo() {
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(" Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(" Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append("\n************ FIRMWARE ************\n");
        errorReport.append(" SDK: ");
        errorReport.append(Build.VERSION.SDK_INT);
        errorReport.append(" SDK NAME: ");
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                errorReport.append(fieldName);
            }
        }

        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        Log.print("------------ " + errorReport.toString());

        return errorReport.toString();
    }

	/*manage image downloading by Glide library*/

    public interface CustomAlertDialogControles {
        void onOkButtonClickListener();
    }


    public interface CustomAlertDialogTwoButtons {
        void leftButtonClickEvent();

        void rightButtonClickEvent();

    }
    public static void setStatusBarColor(AppCompatActivity activity, int color) {
        // change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                window.setStatusBarColor(activity.getResources().getColor(color, null));
            }else{
                window.setStatusBarColor(activity.getResources().getColor(color));
            }
        }
    }
    public static String getDeviceId(Context context){
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static String loadJSONFromAsset(Context mContext, String filename) {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
