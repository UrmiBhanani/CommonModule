package com.androidcommonlibrary.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.androidcommonlibrary.R;
import com.androidcommonlibrary.constants.FontConstants;
import com.androidcommonlibrary.util.TypefaceManager;


/**
 * Custom CheckBox to set different fonts.
 * 
 * "fontName" is the key attribute to set fonts from XML Layouts
 * 
 * And Applies padding b/w drawable and textview's correctly below to 17 Version's.
 * 
 * @author suryaprakash.konduru
 * 
 */
public class FancyCheckBox extends CheckBox {

	public FancyCheckBox(Context context) {
		super(context);
		init(null);
		isInEditMode();
	}

	public FancyCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
		isInEditMode();

	}

	public FancyCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		isInEditMode();
	}

	/**
	 * Reads font name attribute from attribute and sets font to TextView
	 * 
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

		if (!isInEditMode() && attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.Fonts);
			Integer fontName = a.getInteger(R.styleable.Fonts_fontName,
					0);
			if (fontName != null) {
				TypefaceManager.setTypeface(this, FontConstants
						.getFontNameFromFontValue(fontName));
			}
			a.recycle();
		}
	}

	@Override
	public int getCompoundPaddingLeft() {

		// Workarround for version codes < Jelly bean 4.2
		// The system does not apply the same padding. Explantion:
		// http://stackoverflow.com/questions/4037795/android-spacing-between-checkbox-and-text/4038195#4038195

		int compoundPaddingLeft = super.getCompoundPaddingLeft();

		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Drawable drawable = getResources().getDrawable(
					R.drawable.checkbox_background);
			return compoundPaddingLeft
					+ (drawable != null ? drawable.getIntrinsicWidth() : 0);
		} else {
			return compoundPaddingLeft;
		}*/
		return compoundPaddingLeft;

	}
}
