package com.example.widgetpie;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

/*
 *
 *Created by Guang on 2014年6月23日
 *
 */
public class PieChart extends View {

	private Context mContext;
	private boolean mIsShewList;
	private float mTextSize;
	private int mTextColor;
	private float mListTextSize;
	private int mListTextColor;
	private String mTypeText;

	private List<PieViewItem> mDateItems;
	private RectF mMainRect;
	private RectF mPieRect;
	private RectF mListItemRect;
	private TextPaint mPaintText;
	private Paint mPaintDao;

	private float mListDaoRadius = 0;
	private float mListRatioTextWidth = 0;
	private float mListDesTextWidth = 0;
	private float mListWidth = 0;
	private float mListSpace = 0;
	private float mRowHeight = 0;
	private float mRowSpace = 0;
	private float mDefaultHeight = 0;
	private float mCenterRadius = 0;
	
	
	

	public PieChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initDate();
		initUI(context, attrs);
	}

	private void initDate() {
		mDateItems = new ArrayList<PieViewItem>();
		PieViewItem item1 = new PieViewItem();
		item1.mTypeName = "信用卡还款";
		item1.mTypeValue = 2;
		item1.mTypeColor = 0xFF331100;
		mDateItems.add(item1);

		PieViewItem item2 = new PieViewItem();
		item2.mTypeName = "信用卡还款";
		item2.mTypeValue = 5;
		item2.mTypeColor = 0xFF55FFFF;
		mDateItems.add(item2);

		PieViewItem item3 = new PieViewItem();
		item3.mTypeName = "餐饮";
		item3.mTypeValue = 8;
		item3.mTypeColor = 0xFFFFFF00;
		mDateItems.add(item3);

		PieViewItem item4 = new PieViewItem();
		item4.mTypeName = "餐饮";
		item4.mTypeValue = 8;
		item4.mTypeColor = 0xFFFFFF00;
		mDateItems.add(item4);
//
//		 PieViewItem item5 = new PieViewItem();
//		 item5.mTypeName = "餐饮";
//		 item5.mTypeValue = 8;
//		 item5.mTypeColor = 0xFFFFFF00;
//		 mDateItems.add(item5);
		
		//
		// PieViewItem item6 = new PieViewItem();
		// item6.mTypeName = "餐饮";
		// item6.mTypeValue = 8;
		// item6.mTypeColor = 0xFFFFFF00;
		// mDateItems.add(item6);
		//
		// PieViewItem item7 = new PieViewItem();
		// item7.mTypeName = "餐饮";
		// item7.mTypeValue = 8;
		// item7.mTypeColor = 0xFFFFFF00;
		// mDateItems.add(item7);
		//
		//
		// PieViewItem item8 = new PieViewItem();
		// item8.mTypeName = "餐饮";
		// item8.mTypeValue = 8;
		// item8.mTypeColor = 0xFFFFFF00;
		// mDateItems.add(item8);
		//
		//
		// PieViewItem item9 = new PieViewItem();
		// item9.mTypeName = "餐饮";
		// item9.mTypeValue = 8;
		// item9.mTypeColor = 0xFFFFFF00;
		// mDateItems.add(item9);

		countAngle(mDateItems);
	}

	private void initUI(Context context, AttributeSet attrs) {
		mPaintText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mPaintDao = new Paint(Paint.ANTI_ALIAS_FLAG);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.WidgetPie);
		mIsShewList = typedArray.getBoolean(R.styleable.WidgetPie_showList,
				true);
		mTextColor = typedArray.getColor(R.styleable.WidgetPie_textColor,
				0XFFFFFF);
		mTextSize = typedArray
				.getDimension(R.styleable.WidgetPie_textSize, 24);
		mListTextSize = dip2px(mContext, 12);
		mListTextColor = Color.BLACK;
		mTypeText = typedArray.getString(R.styleable.WidgetPie_typeText);
		mMainRect = new RectF();
		mListItemRect = new RectF();
		mPieRect = new RectF();


		mDefaultHeight = dip2px(mContext, 150);

		if (!mIsShewList)
			return;
		// 计算列表相关参数
		mListSpace = dip2px(mContext, 10);
		mListDaoRadius = dip2px(mContext, 10);
		mRowSpace = dip2px(mContext, 8);
		mRowHeight = mListDaoRadius * 3;

		mPaintText.setTextSize(mListTextSize);
		mPaintText.setColor(mListTextColor);
		Rect boundsText = new Rect();
		mPaintText.getTextBounds("信用卡还款", 0, "信用卡还款".length(), boundsText);
		Rect boundsNum = new Rect();
		mPaintText.getTextBounds("20%", 0, "20%".length(), boundsNum);

		// 1、百分比字符宽度
		mListRatioTextWidth = boundsNum.width();
		// 2、类型描述宽度
		mListDesTextWidth = boundsText.width();
		
		typedArray.recycle();

	}

	private void InitDrawParams() {
		int canvasHeight = getHeight();
		int canvasWidth = getWidth();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();

		mMainRect.union(paddingLeft, paddingTop, canvasWidth - paddingRight,
				canvasHeight - paddingBottom);

	}

	private void countAngle(List<PieViewItem> mItems) {
		if (mItems == null || mItems.size() == 0) {
			return;
		}
		float sum = 0;
		for (PieViewItem item : mItems) {
			sum += item.mTypeValue;
		}

		for (int i = 0; i < mItems.size(); i++) {
			if (i == 0) {
				mItems.get(i).mStartAngle = 0;
			} else {
				mItems.get(i).mStartAngle = mItems.get(i - 1).mStartAngle
						+ mItems.get(i - 1).mSweepAngle;
			}
			mItems.get(i).mRatio = (mItems.get(i).mTypeValue * 100) / sum;
			mItems.get(i).mSweepAngle = (mItems.get(i).mTypeValue * 360) / sum;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		InitDrawParams();
		drawPie(canvas);
		drawList(canvas);
		drawCenter(canvas);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int defaultWidth = measureWidth(widthMeasureSpec);
		int defaultHeight = measureHeight(heightMeasureSpec);

		setMeasuredDimension(defaultWidth, defaultHeight);

	}

	private int measureWidth(int widthMeasureSpec) {
		if (getLayoutParams().width == LayoutParams.WRAP_CONTENT) {
			float width = 0;
			if (mIsShewList) {
				width += getListWidth();
				width += mRowSpace;
				if (mDateItems.size() >= 5) {
					width += mDateItems.size() * (mRowHeight + mRowSpace);
				} else {
					width += mDefaultHeight;
				}
				width += getPaddingLeft();
				width += getPaddingRight();
			} else {
				width += mDefaultHeight;
				width += getPaddingLeft();
				width += getPaddingRight();
			}

			if (width > getDefaultSize(getSuggestedMinimumWidth(),
					widthMeasureSpec)) {
				width = getDefaultSize(getSuggestedMinimumWidth(),
						widthMeasureSpec);
			}
			return (int) width;
		}
		return getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
	}

	private int measureHeight(int heightMeasureSpec) {
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
			float height = 0;
			if (mIsShewList && mDateItems.size() >= 5) {
				height += mDateItems.size() * (mRowHeight + mRowSpace);
				height += getPaddingTop();
				height += getPaddingBottom();
			} else {
				height += mDefaultHeight;
				height += getPaddingTop();
				height += getPaddingBottom();
			}

			return (int) height;
		}
		
		return getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
	}

	private float getListWidth() {
		if (!mIsShewList)
			return 0;
		if (mListWidth == 0) {
			mListWidth += mListDesTextWidth;
			mListWidth += mListRatioTextWidth;
			mListWidth += mListDaoRadius * 2;
			mListWidth += mListSpace * 2;
		}
		return mListWidth;
	}

	// 绘制圆饼
	private void drawPie(Canvas canvas) {
		mPieRect.union(mMainRect);
		mPieRect.right = mMainRect.right - getListWidth();

		if (mPieRect.width() < mPieRect.height()) {
			mPieRect.right = mPieRect.right - mRowSpace;
			mPieRect.bottom = mPieRect.top + mPieRect.width();
		} else {
			float space = (mPieRect.right - mPieRect.height()) / 2;
			if (space < mRowSpace) {
				space = mRowSpace;
			}
			mPieRect.right = mPieRect.right - space;
			mPieRect.left = mPieRect.right - mPieRect.height();
		}
		for (PieViewItem item : mDateItems) {
			mPaintDao.setColor(item.mTypeColor);
			canvas.drawArc(mPieRect, item.mStartAngle, item.mSweepAngle, true,
					mPaintDao);
		}

	}
	
	//绘制圆心
	private void drawCenter(Canvas canvas){
		if (mTypeText == null || TextUtils.isEmpty(mTypeText)) return;
		mCenterRadius = mPieRect.height()/4;
		mPaintDao.setColor(Color.WHITE);
		canvas.drawCircle(mPieRect.centerX(), mPieRect.centerY(), mCenterRadius, mPaintDao);
		
		mPaintText.setTextSize(mTextSize);
		mPaintText.setColor(mTextColor); 
		mPaintText.setFakeBoldText(false);

		//获取文字宽度
		String[] strings = mTypeText.split("\n");
		int width = 0;
		for (String string : strings) {
			if (width < mPaintText.measureText(string)) {
				width = (int)mPaintText.measureText(string);
			}
		}
		//自动换行
		StaticLayout layout = new StaticLayout(mTypeText, mPaintText, width, Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
		canvas.save();
		canvas.translate(mPieRect.centerX() - layout.getWidth() /2, mPieRect.centerY() - layout.getHeight() /2);
		layout.draw(canvas);
		canvas.restore();
	}

	// 绘制列表
	private void drawList(Canvas canvas) {
		if (!mIsShewList)
			return;

		mListItemRect.union(mMainRect.right - getListWidth(), mMainRect.top,
				mMainRect.right, mMainRect.top + mRowHeight);

		mPaintText.setTextSize(mTextSize);
		mPaintText.setColor(mTextColor);

		FontMetricsInt fontMetrics = mPaintText.getFontMetricsInt();
		float baseline;
		float ratioX = mListItemRect.left + mListDaoRadius*2 + mListSpace;
		float typeNameX = mListItemRect.left + mListDaoRadius*2 + mListSpace
				+ mListRatioTextWidth + mListSpace;

		mPaintText.setTextSize(mListTextSize);
		mPaintText.setColor(mListTextColor);
		
		for (int i = 0; i < mDateItems.size(); i++) {
			mPaintDao.setColor(mDateItems.get(i).mTypeColor);
			canvas.drawCircle(mListItemRect.left + mListDaoRadius,
					mListItemRect.bottom - mListItemRect.height() / 2, mListDaoRadius,
					mPaintDao);

			// 计算出文本的中心位置
			baseline = mListItemRect.top
					+ (mListItemRect.bottom - mListItemRect.top
							- fontMetrics.bottom + fontMetrics.top) / 2
					- fontMetrics.top;
			mPaintText.setFakeBoldText(true);
			canvas.drawText((int) mDateItems.get(i).mRatio + "%", ratioX,
					baseline, mPaintText);
			mPaintText.setFakeBoldText(false);
			canvas.drawText(mDateItems.get(i).mTypeName, typeNameX, baseline,
					mPaintText);

			mListItemRect.top += mListDaoRadius*2 + mRowSpace;
			mListItemRect.bottom += mListDaoRadius*2 + mRowSpace;
		}

	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}
