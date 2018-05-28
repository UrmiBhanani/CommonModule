package com.androidcommonlibrary.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.androidcommonlibrary.R;
import com.androidcommonlibrary.constants.FontConstants;
import com.androidcommonlibrary.util.TypefaceManager;


/**
 * Custom EditText to set different fonts. 
 * 
 *  "fontName" is the key attribute to set fonts from XML Layouts
 * 
 * @author suryaprakash.konduru
 *
 */
public class FancyRadioButton extends RadioButton {

	public FancyRadioButton(Context _Context) {

		super(_Context);
		init(null);
		isInEditMode();
	}

	public FancyRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
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
