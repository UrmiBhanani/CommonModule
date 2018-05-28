package com.androidcommonlibrary.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/*
 * TypefaceManager effectively manages fonts in cache.
 */
public class TypefaceManager {

	/** The Typeface cache, keyed by their asset file name. */
	private static final Map<String, Typeface> mCache = new HashMap<String, Typeface>();

	/*
	 * Gets typeface from cache if already exist or creates from assets if not
	 * exist
	 */
	public static Typeface getTypeface(Context context, String fontPath) {

		synchronized (mCache) {
			if (!mCache.containsKey(fontPath)) {
				Typeface t = Typeface.createFromAsset(context.getAssets(),
						fontPath);
				mCache.put(fontPath, t);
			}
		}
		return mCache.get(fontPath);

	}

	/*
	 * sets type faces to textview
	 */
	public static boolean setTypeface(TextView target, String fontPath) {

		Typeface tf = null;
		try {
			tf = getTypeface(target.getContext(), fontPath);
		} catch (Exception e) {
			return false;
		}
		target.setTypeface(tf);
		return true;

	}

}
