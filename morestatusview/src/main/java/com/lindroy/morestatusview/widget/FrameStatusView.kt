package com.lindroy.morestatusview.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.children
import com.lindroy.morestatusview.MoreStatusView
import com.lindroy.morestatusview.R
import com.lindroy.morestatusview.STATUS_CONTENT
import com.lindroy.morestatusview.constants.NULL_RESOURCE_ID
import com.lindroy.morestatusview.constants.STATUS_EMPTY
import com.lindroy.morestatusview.constants.STATUS_ERROR
import com.lindroy.morestatusview.constants.STATUS_LOADING

/**
 * @author Lin
 * @date 2019/12/22
 * @function
 */
class FrameStatusView : FrameLayout {

    private var noNetworkViewId = 0
    private var emptyView: View? = null
    private var loadingView: View? = null
    private var errorView:View? = null
    private var curViewStatus = STATUS_CONTENT
    private var defaultLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    private var viewStatusListener: ((oldStatus: Int, newStatus: Int) -> Unit)? = null
    private var clickListener: ((view: View) -> Unit)? = null
    private var statusParams = MoreStatusView.instance
    private var emptyParams = statusParams.emptyInfo
    private var loadingParams = statusParams.loadingInfo
    private var errorParams = statusParams.errorInfo

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.MoreStatusView, defStyleAttr, 0).apply {
            emptyParams.layoutId = getResourceId(R.styleable.MoreStatusView_msv_emptyView, emptyParams.layoutId)
            loadingParams.layoutId = getResourceId(R.styleable.MoreStatusView_msv_loadingView, loadingParams.layoutId)
            errorParams.layoutId =  getResourceId(R.styleable.MoreStatusView_msv_errorView, errorParams.layoutId)
            noNetworkViewId = getResourceId(R.styleable.MoreStatusView_msv_noNetworkView, NULL_RESOURCE_ID)
            recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    fun showContent() {

    }

    @JvmOverloads
    fun showEmpty(view: View? = null, layoutParams: ViewGroup.LayoutParams = defaultLayoutParams) {
        if (emptyView == null) {
            emptyView = if (view == null) {
                checkLayoutId(emptyParams.layoutId)
                inflateView(emptyParams.layoutId)
            } else view
            emptyView!!.tag = STATUS_EMPTY
            addView(emptyView, 0, layoutParams)
        } else {
            if (view != null) {
                removeView(loadingView)
                emptyView = view
                emptyView!!.tag = STATUS_EMPTY
                addView(loadingView, 0, layoutParams)
            }
        }
        showViewByStatus(STATUS_EMPTY)
    }

    @JvmOverloads
    fun showEmpty(@LayoutRes layoutId: Int, layoutParams: ViewGroup.LayoutParams = defaultLayoutParams) =
        showEmpty(inflateView(layoutId), layoutParams)

    @JvmOverloads
    fun showLoading(view: View? = null, layoutParams: ViewGroup.LayoutParams = defaultLayoutParams) {
        if (loadingView == null) {
            loadingView = if (view == null) {
                checkLayoutId(loadingParams.layoutId)
                inflateView(loadingParams.layoutId)
            } else view
            loadingView!!.tag = STATUS_LOADING
            addView(loadingView, 0, layoutParams)
        } else {
            if (view != null) {
                removeView(loadingView)
                loadingView = view
                loadingView!!.tag = STATUS_LOADING
                addView(loadingView, 0, layoutParams)
            }
        }
        showViewByStatus(STATUS_LOADING)
    }

    @JvmOverloads
    fun showLoading(@LayoutRes layoutId: Int, layoutParams: ViewGroup.LayoutParams = defaultLayoutParams) =
        showLoading(inflateView(layoutId), layoutParams)

    @JvmOverloads
    fun showError(view: View? = null, layoutParams: ViewGroup.LayoutParams = defaultLayoutParams,vararg clickViewIds:Int){
        if (errorView == null){
            errorView = if (view == null){
                checkLayoutId(errorParams.layoutId)
                inflateView(errorParams.layoutId)
            } else view
            errorView!!.tag = STATUS_ERROR
            addView(errorView,0,layoutParams)
        }else{
           if (view != null) {
               removeView(errorView)
               errorView = view
               errorView!!.tag = STATUS_ERROR
               addView(errorView,0,layoutParams)
           }
        }
        setOnRetryViewClickListener(errorView!!,if (clickViewIds.isNotEmpty()) clickViewIds.toList() else errorParams.retryViewIds)
        showViewByStatus(STATUS_ERROR)
    }

    @JvmOverloads
    fun showError(@LayoutRes layoutId: Int, layoutParams: ViewGroup.LayoutParams = defaultLayoutParams,
                  vararg clickViewIds:Int) =
        showError(inflateView(layoutId),layoutParams,*clickViewIds)

    /**
     * 设置点击事件
     */
    fun setOnViewsClickListener(clickListener: (view: View) -> Unit){
        this.clickListener = clickListener
    }


    /**
     * 改变当前视图状态
     */
    private fun changeViewStatus(newStatus: Int) {
        if (curViewStatus == newStatus) {
            return
        }
        viewStatusListener?.invoke(curViewStatus, newStatus)
        curViewStatus = newStatus
    }

    private fun inflateView(layoutId: Int) = LayoutInflater.from(context).inflate(layoutId, null)

    private fun setOnRetryViewClickListener(parent: View, retryViewIds: List<Int>) {
        clickListener?.also {
            retryViewIds.forEach {id->
                parent.findViewById<View>(id).setOnClickListener {view->
                    it.invoke(view)
                }
            }
        }
    }

    private fun checkLayoutId(@LayoutRes layoutId: Int) {
        if (layoutId == NULL_RESOURCE_ID) {
            throw NullPointerException("请先设置该状态视图的布局！")
        }
    }

    private fun showViewByStatus(status: Int) {
        children.forEach {
            it.visibility = if (it.tag == status) View.VISIBLE else View.GONE
        }
        changeViewStatus(status)
    }

}