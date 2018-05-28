package com.androidcommonlibrary.http;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Keyur on 13/1/16.
 */
public class AssetsReader {

    Context mContext;

    public AssetsReader(Context _Context) {
        mContext = _Context;
    }

    public String getAssetsJsonData(String fileName) {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = mContext.getAssets().open(fileName);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return  buf.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
