package audiorecordUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class RecordWave extends View{
	private Paint mPaint; 
	float viewWidth, viewHeight, radius = 0, maxRadius = 255;
	boolean trigger = false;
	public RecordWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
        
        mPaint.setStyle(Style.FILL); 
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = this.getWidth();
		viewHeight = this.getHeight();	
		maxRadius = viewWidth / 2;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(trigger)
		{
			if(radius >= maxRadius)
				radius = 0;
			int alpha = (int) (radius * -255/maxRadius + 255); 
			mPaint.setColor(Color.argb(alpha, 200, 84, 84));
			mPaint.setStyle(Style.FILL);
			canvas.drawCircle(viewWidth/2, viewWidth/2, radius, mPaint);
			
			mPaint.setStyle(Style.STROKE);
			mPaint.setColor(Color.argb(alpha, 133, 10, 10));
			mPaint.setStrokeWidth(4);
			canvas.drawCircle(viewWidth/2, viewWidth/2, radius, mPaint);
			radius += 2;
			this.postInvalidate();
		}
	}
	public void setTrigger()
	{
		trigger = !trigger;
	}

}
