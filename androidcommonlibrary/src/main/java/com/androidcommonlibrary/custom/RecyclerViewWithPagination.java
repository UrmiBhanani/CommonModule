package com.androidcommonlibrary.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.androidcommonlibrary.R;
import com.androidcommonlibrary.util.EndlessRecyclerViewScrollListener;

import butterknife.ButterKnife;

/**
 * Created by urmila.bhanani on 4/25/2016.
 */

public class RecyclerViewWithPagination extends RecyclerView {

    private int mType = 1;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    OnLoadMoreListener onLoadMoreListener;


    public RecyclerViewWithPagination(Context context) {
        super(context);
        init(null, 0);
    }

    public RecyclerViewWithPagination(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RecyclerViewWithPagination(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        //do stuff here

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RecyclerViewWithPagination, defStyle, 0);

        if (a.hasValue(R.styleable.RecyclerViewWithPagination_list_type)){
            mType = a.getInt(
                    R.styleable.RecyclerViewWithPagination_list_type, 1);
        }

        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), mType);
        int mSpanCount = 2;
        int mOrientation = 1;

        if (a.hasValue(R.styleable.RecyclerViewWithPagination_list_spanCount)){
            mSpanCount = a.getInt(R.styleable.RecyclerViewWithPagination_list_spanCount, 2);
        }
        if (a.hasValue(R.styleable.RecyclerViewWithPagination_list_orientation)){
            mOrientation = a.getInt(R.styleable.RecyclerViewWithPagination_list_orientation, StaggeredGridLayoutManager.VERTICAL);
        }
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(mSpanCount, mOrientation);

        if (mType == 1) {
            setLayoutManager(linearLayoutManager);
        }else if (mType == 2) {
            setLayoutManager(gridLayoutManager);
        } else {
            setLayoutManager(mStaggeredGridLayoutManager);
        }
        // Add the scroll listener based on layout type
        // 1 for LinearLayoutManager
        // 2 for GridLayoutManager
        // 3 for StaggeredGridLayoutManager
        switch (mType){
            case 1:
                addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int previousTotalItemCount) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        onLoadMoreListener.onLoadMoreData(page, previousTotalItemCount);
                    }
                });
                break;
            case 2:
                addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int previousTotalItemCount) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        onLoadMoreListener.onLoadMoreData(page, previousTotalItemCount);
                    }
                });
                break;
            case 3:
                addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredGridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int previousTotalItemCount) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        onLoadMoreListener.onLoadMoreData(page, previousTotalItemCount);
                    }
                });
                break;
        }


    }

    /**
     * Set listener for perform pagination in your activity
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener{
        // Defines the process for actually loading more data based on page
        void onLoadMoreData(int page, int previousTotalItemCount);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
