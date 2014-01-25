package com.deskpro.mobile.layouts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

public class BorderBottomLayout extends FrameLayout {
	private Paint mPaint;
	
	public BorderBottomLayout(Context context) {
		super(context);
		this.setWillNotDraw(false);
	}
	
	public BorderBottomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setWillNotDraw(false);
	}
	
	public BorderBottomLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setWillNotDraw(false);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(mPaint == null) {
			mPaint = new Paint();
			mPaint.setColor(Color.WHITE);
		}
		
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		
		Resources r = getResources();
		float lineThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10, r.getDisplayMetrics());
		
		canvas.drawRect(0, height - lineThickness, width, height, mPaint);
	}

}
