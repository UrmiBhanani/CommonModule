package com.androidcommonlibrary.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.androidcommonlibrary.R;
import com.androidcommonlibrary.constants.FontConstants;
import com.androidcommonlibrary.util.TypefaceManager;


/**
 * Custom Button to set different fonts.
 * 
 * "fontName" is the key attribute to set fonts from XML Layouts
 * 
 * @author suryaprakash.konduru
 * 
 */
public class FancyButton extends Button {

	public FancyButton(Context _Context) {

		super(_Context);
		init(null);
		isInEditMode();
	}

	public FancyButton(Context context, AttributeSet attrs) {
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
			Integer fontName = a.getInteger(R.styleable.Fonts_fontName, 0);
			if (fontName != null) {
				TypefaceManager.setTypeface(this, FontConstants
						.getFontNameFromFontValue(fontName));
			}
			a.recycle();
		}
	}

}
