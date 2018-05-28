package com.androidcommonlibrary.constants;

import android.os.Environment;

/**
 * Created by Administrator on 5/3/2016.
 */
public class Constants {

    /* Storage files directory */
    public static String APP_HOME = Environment.getDataDirectory()
            .getPath() + "/UtilLib";
    public static String DIR_LOG = APP_HOME + "/Log";
    public static String LOG_ZIP = APP_HOME + "/UtilLib.zip";
    public static String DIR_IMAGES = APP_HOME + "/data";

    public static String PREF_FILE = "PREF_UtilLib";

}
