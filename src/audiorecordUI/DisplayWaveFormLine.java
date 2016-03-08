package audiorecordUI;

import mathTools._math;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.Log;

public class DisplayWaveFormLine extends Thread{
	OscilloGraph og = null;
	int drawTime = 1000 / 15;
	long lastTime;
	int baseLine, sfvWidth, sfvHeight;
	int gapWidth = 8, lineWidth = 4;
	int pos = 0, threshold = 300;
	short[] disArray;
	int maxLineNum = 0;
	public DisplayWaveFormLine(OscilloGraph og) 
	{
		// TODO Auto-generated constructor stub
		this.og = og;
		baseLine = og.getSFVHeight() / 2;
		sfvHeight = og.getSFVHeight();
		sfvWidth = og.getSFVWidth();
		maxLineNum = sfvWidth / (lineWidth + gapWidth);
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub		
		disArray = null;
		while(!og.isFinish() || !og.isEmpty())
		{
        	short[] data = og.take();
        	if(disArray == null)       	
        		disArray = data.clone();       	
        	else
        		disArray = _math.mergeArray(disArray,data);//将新加的data数据添加到显示数组后边
        	drawWaveForm();     //将disArray中的数据绘制出来
		}	
	}
	private void drawWaveForm() 
	{		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Paint gap = new Paint();
		gap.setColor(Color.TRANSPARENT);
		
		for(int i = pos; i  < disArray.length;i++)
		{		
			Canvas canvas = og.lockCanvas();
			canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
			Log.i("disArray:",Short.toString(disArray[i]));
			
			for(int j = 0;j <= i; j++)
			{			
				if(sfvWidth - (i - j + 1) * (lineWidth + gapWidth) < 0)
					continue;
				if(canvas == null)
					return;	
				float volume = (float) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 200 + 4;
				volume /= 2;
				volume = (float) Math.floor(volume);
				int alpha = (int) ((float)(Math.abs(disArray[j]) > threshold ? Math.abs(disArray[j]) : 0) / 32768.0) * 255 + 150;
				alpha = (alpha > 255) ? 255 : alpha;
				paint.setAlpha(alpha);
				Log.i("volume:",Float.toString(volume));	
				canvas.drawRect(sfvWidth - (i - j + 1) * (lineWidth + gapWidth), (baseLine + volume), sfvWidth - (i - j) * (lineWidth + gapWidth ) - gapWidth, (baseLine - volume), paint);				
			}	
 			og.unlockCanvasAndPost(canvas);
		} 
		if(disArray.length > maxLineNum)
			disArray = _math.copyByIndex(disArray, disArray.length - maxLineNum, disArray.length - 1);			
		pos = disArray.length;
	}

	
}
