package audiorecordUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RecordButton extends View{
	float smallRadius, bigRadius, radius, viewWidth, viewHeight, blackBackRadius, whiteBackRadius;
	float speed = 7;
	float roundX, roundY, rx , ry;
	boolean clicked = false, bigger = true, first = true;
	RectF rect ;
	private Paint mPaint; 

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Style.FILL); 
       // this.setOnClickListener(new clicked());     
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = this.getWidth();
		viewHeight = this.getHeight();	
		float minRadius = Math.min(viewHeight, viewWidth);
		roundX = viewWidth / 2;
		roundY = viewHeight / 2;		
		blackBackRadius = (float) (minRadius * 0.4);
		whiteBackRadius = (float) (minRadius * 0.5);
		smallRadius =(float) (minRadius * 0.2);
		bigRadius = (float) (minRadius * 0.38);		
		rect  = new RectF(roundX - smallRadius, roundY - smallRadius, roundX + smallRadius, roundY + smallRadius);
		ry = rx = smallRadius/2;	
		radius = bigRadius;
		
		Log.i("msg",Integer.toString(this.getHeight()) +" " +Integer.toString(this.getWidth()));
	}
	 @Override
	protected void onDraw(Canvas canvas) 
	  {	  	  
		  int red = Color.argb(255,158,82,82);
		 /* mPaint.setColor(Color.BLACK);
		  canvas.drawRect(0, 0, viewWidth, viewHeight, mPaint);*/
		  mPaint.setColor(Color.WHITE);//按钮白底
		  canvas.drawCircle(roundX, roundY, whiteBackRadius, mPaint);
		  mPaint.setColor(Color.BLACK);//按钮黑底
		  canvas.drawCircle(roundX, roundY, blackBackRadius, mPaint);
		  mPaint.setColor(red);
		 /* if(first)
		  {	
			  canvas.drawCircle(roundX, roundY, radius, mPaint);
			  canvas.drawRoundRect(rect, rx, ry, mPaint);
			  first =!first;
			  return;
		  }*/
		  
		  if(clicked && bigger && radius > smallRadius)
		  {			 
			  canvas.drawCircle(roundX, roundY, radius, mPaint);	
			  canvas.drawRoundRect(rect, rx, ry, mPaint);
			  radius-=speed;
			  postInvalidate();
			  return;
		  }
		  if(!clicked && !bigger && radius < bigRadius)
		  {
			  canvas.drawCircle(roundX, roundY, radius, mPaint);	
			  canvas.drawRoundRect(rect, rx, ry, mPaint);
			  radius+=speed;
			  postInvalidate();
			  return;
		  }
		  if(clicked && bigger && radius <= smallRadius)
		  {
			  canvas.drawCircle(roundX, roundY, radius, mPaint);
			  canvas.drawRoundRect(rect, rx, ry, mPaint);
			  return ;
		  }
		  else if(!clicked && !bigger && radius >= bigRadius)
		  {
			  canvas.drawCircle(roundX, roundY, radius, mPaint);	
			  canvas.drawRoundRect(rect, rx, ry, mPaint);
			  bigger = !bigger;
			  return;
		  }
		  canvas.drawCircle(roundX, roundY, radius, mPaint);
		  canvas.drawRoundRect(rect, rx, ry, mPaint);
		  
	  }
	 public void trigeAnnimation()
	 {
		 if(!clicked)
			{
				clicked = !clicked;				
				RecordButton.this.postInvalidate();
			}
			else
			{
				clicked = !clicked;
				bigger = !bigger;
				RecordButton.this.postInvalidate();
			}
	 }
	
}
