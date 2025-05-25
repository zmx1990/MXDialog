package com.mx.dialog.base

import android.content.Context
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.mx.dialog.R
import com.mx.dialog.tip.MXCardPosition
import com.mx.dialog.utils.MXDrawableUtils
import com.mx.dialog.utils.MXUtils

/**
 * 集成内容定位的功能
 * 详情见：setPosition()
 */
abstract class MXBaseCardDialog(context: Context) : MXBaseDialog(context) {
    private var includeStatusBarHeight: Boolean = false
    private var includeNavigationBarHeight: Boolean = false

    private var dialogBackgroundColor: Int? = null
    private var cardBackgroundRadiusDP = 12f
    private var cardMarginDP = RectF(25f, 25f, 25f, 25f)
    private var position = MXCardPosition.CENTER
    private var mxRootLay: ViewGroup? = null
    private var mxCardLay: ViewGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mx_dialog_base_card)
        initView()
    }

    private fun initView() {
        if (mxRootLay == null) mxRootLay = findViewById(R.id.mxRootLay)
        if (mxCardLay == null) mxCardLay = findViewById(R.id.mxCardLay)

        val content = LayoutInflater.from(context).inflate(getContentLayoutId(), mxCardLay, false)
        mxCardLay?.removeAllViews()
        mxCardLay?.addView(
            content,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }


    abstract fun getContentLayoutId(): Int

    override fun onStart() {
        super.onStart()
        initDialog()
    }

    private val marginLeft: Int get() = MXUtils.dp2px(context, cardMarginDP.left)
    private val marginTop: Int
        get() {
            var top = MXUtils.dp2px(context, cardMarginDP.top)
            if (includeStatusBarHeight) {
                top += MXUtils.getStatusBarHeight(context)
            }
            return top
        }
    private val marginRight: Int get() = MXUtils.dp2px(context, cardMarginDP.right)
    private val marginBottom: Int
        get() = MXUtils.dp2px(
            context,
            cardMarginDP.bottom
        ) + (if (includeNavigationBarHeight) MXUtils.getNavigationBarHeight(context) else 0)

    protected open fun initDialog() {
        initRootView(mxRootLay ?: return)
        initCardView(mxCardLay ?: return)
    }

    open fun initRootView(rootLay: ViewGroup) {
        rootLay.setOnClickListener {
            if (isCanceledOnTouchOutside()) {
                onBackPressed()
            }
        }
        rootLay.setPadding(marginLeft, marginTop, marginRight, marginBottom)
        val color = dialogBackgroundColor
            ?: context.resources.getColor(R.color.mx_dialog_color_background_root)
        rootLay.setBackgroundColor(color)
    }

    open fun initCardView(cardLay: ViewGroup) {
        cardLay.setOnClickListener { }
        if (cardBackgroundRadiusDP >= 0) {
            cardLay.background = MXDrawableUtils.buildGradientDrawable(
                context, R.color.mx_dialog_color_background_content,
                cardBackgroundRadiusDP
            )
        } else {
            cardLay.setBackgroundResource(R.drawable.mx_dialog_card_bg)
        }

        val lp = (cardLay.layoutParams as FrameLayout.LayoutParams?)
        lp?.gravity = position.gravity
        lp?.width = MXUtils.getScreenWidth(context) - marginLeft - marginRight

        // 平板
        if (MXUtils.getScreenWidthDP(context) > 600) {
            lp?.width = MXUtils.getScreenWidth(context) / 2
        }

        cardLay.layoutParams = lp
        cardLay.translationX =
            MXUtils.dp2px(context, position.translationX ?: 0).toFloat()
        cardLay.translationY =
            MXUtils.dp2px(context, position.translationY ?: 0).toFloat()
    }

    /**
     * 设置弹窗背景颜色
     */
    fun setDialogBackGroundColor(color: Int) {
        dialogBackgroundColor = color

        initDialog()
    }

    /**
     * 设置内容ViewGroup 背景圆角半径
     */
    fun setCardBackgroundRadius(radiusDP: Float) {
        cardBackgroundRadiusDP = radiusDP

        initDialog()
    }

    fun getCardBackgroundRadiusDP() = cardBackgroundRadiusDP

    /**
     * 设置弹窗位置
     */
    fun setCardPosition(position: MXCardPosition) {
        this.position = position

        initDialog()
    }

    /**
     * 设置内容外边距
     * 单位：DP
     * @param margin 左/右/上/下 边框宽度
     * @param includeStatusBarHeight 上边框宽度是否加上状态栏高度
     * @param includeNavigationBarHeight 下边框宽度是否加上导航栏高度
     */
    fun setCardMargin(
        margin: Float,
        includeStatusBarHeight: Boolean = false,
        includeNavigationBarHeight: Boolean = false
    ) {
        cardMarginDP = RectF(margin, margin, margin, margin)
        this.includeStatusBarHeight = includeStatusBarHeight
        this.includeNavigationBarHeight = includeNavigationBarHeight

        initDialog()
    }

    /**
     * 设置内容外边距
     * 单位：DP
     * @param horizontal 左/右 边框宽度
     * @param vertical 上/下 边框宽度
     * @param includeStatusBarHeight 上边框宽度是否加上状态栏高度
     * @param includeNavigationBarHeight 下边框宽度是否加上导航栏高度
     */
    fun setCardMargin(
        horizontal: Float, vertical: Float,
        includeStatusBarHeight: Boolean = false,
        includeNavigationBarHeight: Boolean = false
    ) {
        cardMarginDP = RectF(horizontal, vertical, horizontal, vertical)
        this.includeStatusBarHeight = includeStatusBarHeight
        this.includeNavigationBarHeight = includeNavigationBarHeight

        initDialog()
    }

    /**
     * 设置内容外边距
     * 单位：DP
     * @param left 左 边框宽度
     * @param top 上 边框宽度
     * @param right 右 边框宽度
     * @param bottom 下 边框宽度
     * @param includeStatusBarHeight 上边框宽度是否加上状态栏高度
     * @param includeNavigationBarHeight 下边框宽度是否加上导航栏高度
     */
    fun setCardMargin(
        left: Float, top: Float, right: Float, bottom: Float,
        includeStatusBarHeight: Boolean = true,
        includeNavigationBarHeight: Boolean = true
    ) {
        cardMarginDP = RectF(left, top, right, bottom)
        this.includeStatusBarHeight = includeStatusBarHeight
        this.includeNavigationBarHeight = includeNavigationBarHeight

        initDialog()
    }
}