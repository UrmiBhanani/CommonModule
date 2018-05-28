package com.androidcommonlibrary.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.androidcommonlibrary.R;
import com.androidcommonlibrary.constants.FontConstants;
import com.androidcommonlibrary.util.TypefaceManager;


/**
 * Custom TextView to set different fonts.
 * 
 * "fontName" is the key attribute to set fonts from XML Layouts
 * 
 * @author suryaprakash.konduru
 * 
 */
public class FancyTextview extends TextView {

	public FancyTextview(Context _Context) {

		super(_Context);
		init(null);
		isInEditMode();
	}

	public FancyTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(attrs);
		isInEditMode();
	}

	public FancyTextview(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs,defStyle);
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

		if (!this.isInEditMode() && attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.Fonts);
			Integer fontName = a.getInteger(R.styleable.Fonts_fontName, 0);
			if (fontName != null) {

				TypefaceManager.setTypeface(this,
						FontConstants.getFontNameFromFontValue(fontName));
			}
			a.recycle();
		}
	}

	/*added for setting font name programatically*/
	public void setFont(FontConstants font) {
		TypefaceManager.setTypeface(this,font.getFontPathName());
	}

}
