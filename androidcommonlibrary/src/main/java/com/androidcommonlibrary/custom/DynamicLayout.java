package com.androidcommonlibrary.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcommonlibrary.util.Utils;

public class DynamicLayout {

	private static LayoutParams mParams_textview, mParams_editext,
			mParams_spinner, mParams_imagview, mParams_autotext,
			mParams_linear;
	private static RelativeLayout.LayoutParams mParams_rel, mParams_imageview,
			mParams_progressbar, mParams_textview_rel;
	
	private static RadioGroup.LayoutParams mParam_radioGrp;
	
	
	public static RadioGroup.LayoutParams setParams_RadioGrp(){
		
		mParam_radioGrp=new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
		
		return mParam_radioGrp;
		
	}

	public static LayoutParams setParams_textview() {

		mParams_textview = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1f);

		return mParams_textview;
	}

	public static LayoutParams setParams_edittext() {

		mParams_editext = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1f);

		return mParams_editext;
	}

	public static LayoutParams setParams_spinner() {

		mParams_spinner = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1f);

		return mParams_spinner;
	}

	public static RelativeLayout.LayoutParams setParams_progressbar(
			Context mContext) {

		mParams_progressbar = new RelativeLayout.LayoutParams(
				(int) (Utils.getDeviceWidth(mContext) * 0.7),25);

		return mParams_progressbar;
	}

	public static LayoutParams setParams_imageview() {

		mParams_imagview = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		return mParams_imagview;
	}

	public static LayoutParams setParams_autotext() {

		mParams_autotext = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		return mParams_autotext;
	}

	public static LayoutParams setParams_linear() {

		mParams_autotext = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		return mParams_autotext;
	}

	public static RelativeLayout.LayoutParams setparams_rel() {

		mParams_rel = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		return mParams_rel;
	}

	public static RelativeLayout.LayoutParams setparams_imageview() {

		mParams_imageview = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		return mParams_imageview;
	}

	public static RelativeLayout.LayoutParams setparams_textview() {

		mParams_textview_rel = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		return mParams_textview_rel;
	}

	public static TextView addTextview(TextView mTxt_dynamic, String mTag,
			String mValue, int mSize) {

		mTxt_dynamic.setTag(mTag);
		mTxt_dynamic.setText(mValue);
		mTxt_dynamic.setTextSize(mSize);
		return mTxt_dynamic;
	}

	public static EditText addEdittext(EditText mEdt_dynamic, String mTag,
			String mHint, int mInputVaule, int mSize) {

		mEdt_dynamic.setTag(mTag);
		mEdt_dynamic.setHint(mHint);
		mEdt_dynamic.setInputType(mInputVaule);
		mEdt_dynamic.setTextSize(mSize);
		return mEdt_dynamic;
	}

	public static EditText addAutoCompleteTextview(
			AutoCompleteTextView mAutotxt_dynamic, String mTag, String mHint,
			int mInputVaule, int mSize) {

		mAutotxt_dynamic.setTag(mTag);
		mAutotxt_dynamic.setHint(mHint);
		mAutotxt_dynamic.setInputType(mInputVaule);
		mAutotxt_dynamic.setTextSize(mSize);
		return mAutotxt_dynamic;
	}

	public static Spinner addSpinner(Spinner mSpi_dynamic, String mTag,
			ArrayAdapter<String> mAdapter) {

		mSpi_dynamic.setTag(mTag);
		mSpi_dynamic.setAdapter(mAdapter);
		return mSpi_dynamic;
	}

	public static ProgressBar addProgressbar(ProgressBar mPro_dynamic,
			String mTag, int mMaxvalue, Drawable mDrawable) {

		mPro_dynamic.setTag(mTag);
		mPro_dynamic.setMax(mMaxvalue);
		mPro_dynamic.setProgressDrawable(mDrawable);
		return mPro_dynamic;
	}

	public static ImageView addImagview(ImageView mImg_dynamic, String mTag,
			int mResourceId) {

		mImg_dynamic.setTag(mTag);
		mImg_dynamic.setImageResource(mResourceId);
		return mImg_dynamic;
	}

	public static LinearLayout addLinearLayout(LinearLayout mLinear_dynamic) {

		mLinear_dynamic.setWeightSum(2);
		mLinear_dynamic.setOrientation(LinearLayout.HORIZONTAL);
		return mLinear_dynamic;
	}

}
