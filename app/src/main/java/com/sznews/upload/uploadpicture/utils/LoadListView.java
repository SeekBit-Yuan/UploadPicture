package com.sznews.upload.uploadpicture.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sznews.upload.uploadpicture.R;

/**
 * Created by qiy on 2018-2-28.
 */

public class LoadListView extends ListView implements AbsListView.OnScrollListener{

    private int mFootViewHeight;
    private View mFootView;
    private int LOADSTATE = 0; //加载的状态

    public LoadListView(Context context) {
        super(context);

        InitFootView(context);
        setOnScrollListener(this);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化脚布局并添加到当前的ListView
        InitFootView(context);
        setOnScrollListener(this);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void InitFootView(Context context) {

        mFootView = View.inflate(context, R.layout.foot_load_item, null);
        mFootView.measure(0, 0); //测量
        mFootViewHeight = mFootView.getMeasuredWidth();
        addFooterView(mFootView);
    }

    //加载完成调用隐藏脚布局，并把mLoadState的状态设置为0，表示未刷新状态
    public void setLoadComplete() {

        LOADSTATE = 0;
        //加载完成后隐藏脚布局
        mFootView.setPadding(0, -mFootViewHeight, 0, 0);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 滑动时一直调用
     * @param view
     * @param firstVisibleItem 当前能看见的第一条item的Id
     * @param visibleItemCount 当前能看见的item的总数
     * @param totalItemCount 所有的item
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        //判断是否滑动到当前listview的最后一个条目
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
            if (LOADSTATE == 0) {
                //取消隐藏
                mFootView.setPadding(0, 0, 0, 0);
                onLoadListener.onLoading();
                LOADSTATE = 1; //设置状态为加载中，放置多次调用onLoading
            }
        }
    }

    //加载更多的回调接口
    private OnLoadListener onLoadListener;

    public void setOnLoadlistener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public interface OnLoadListener {
        void onLoading();
    }
}
